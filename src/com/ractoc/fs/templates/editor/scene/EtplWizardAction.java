/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ractoc.fs.templates.editor.scene;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.sceneexplorer.nodes.actions.AbstractNewSpatialWizardAction;
import com.jme3.gde.core.sceneexplorer.nodes.actions.NewSpatialAction;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;
import com.ractoc.fs.parsers.entitytemplate.TemplateLoader;
import com.ractoc.fs.parsers.entitytemplate.TemplateParser;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;
import java.awt.Component;
import java.awt.Dialog;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.LinkedList;
import javax.swing.JComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="com.ractoc.fs.templates.editor.scene.EtplWizardAction")
// @ActionRegistration(displayName="Open Etpl Wizard")
// @ActionReference(path="Menu/Tools", position=...)
@org.openide.util.lookup.ServiceProvider(service = NewSpatialAction.class)
public final class EtplWizardAction extends AbstractNewSpatialWizardAction {

    private Node node;
    private ProjectAssetManager manager;
    private WizardDescriptor.Panel[] panels;
    private Project project;
    private LinkedList<URL> urls;
    private URLClassLoader loader;

    public EtplWizardAction() {
        name = "Entity Template";
    }

    @Override
    protected Object showWizard(Node node) {
        this.node = node;
        setupManager();
        setupProject();
        setupClassLoader();

        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Entity Template Wizard");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            return wizardDescriptor;
        }
        return null;
    }

    private void setupManager() {
        manager = node.getLookup().lookup(ProjectAssetManager.class);
        if (manager == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
        }
    }

    @Override
    protected Spatial doCreateSpatial(com.jme3.scene.Node parent, Object properties) {
        if (properties != null) {
            return generateTemplateSpatial((WizardDescriptor) properties);
        }
        return null;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new EtplWizardPanel1(node)
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.FALSE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.FALSE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    private Spatial generateTemplateSpatial(WizardDescriptor wizardDescriptor) {
        FileObject fo = (FileObject) wizardDescriptor.getProperty("templateFile");
        Spatial model = null;
        if (fo != null) {
            TemplateParser parser = new TemplateParser();
            parser.setClassLoader(loader);
            try {
                EntityTemplate tpl = parser.parse(fo.getInputStream());
                boolean rendered = false;
                for (EntityComponent component : tpl.getComponents()) {
                    Template template = component.getClass().getAnnotation(Template.class);
                    String modelParameterName = template.model();
                    if (modelParameterName != null && modelParameterName.trim().length() > 0) {
                        String modelMethodName = "get" + modelParameterName.substring(0, 1).toUpperCase() + modelParameterName.substring(1);
                        Method modelMethod = component.getClass().getMethod(modelMethodName);
                        String modelName = (String) modelMethod.invoke(component);
                        model = manager.loadModel(modelName);
                        model.setName(tpl.getName() + " - Entity Template");
                        model.setUserData("templateFileName", fo.getPath().substring(manager.getAssetFolderName().length()));
                        rendered = true;
                        break;
                    }
                }
                if (!rendered) {
                    for (EntityComponent component : tpl.getComponents()) {
                        Template template = component.getClass().getAnnotation(Template.class);
                        ColorRGBA proxyColor = convertProxyColorToRGBA(template.proxyColor());
                        if (proxyColor != null) {
                            Box b = new Box(1, 1, 1);
                            model = new Geometry("Box", b);
                            Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
                            mat.setColor("Color", proxyColor);
                            model.setMaterial(mat);
                            model.setName(tpl.getName() + " - Entity Template");
                            model.setUserData("templateFileName", fo.getPath().substring(manager.getAssetFolderName().length()));
                            rendered = true;
                            break;
                        }
                    }
                }
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return model;
    }

    private void setupClassLoader() {
        SourceGroup[] groups = getSourceGroupFromProject();
        getURLsFromSourceGroups(groups);
        addClassLoader();
    }

    private void setupProject() {
        project = manager.getProject();
    }

    private SourceGroup[] getSourceGroupFromProject() {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        return groups;
    }

    private void getURLsFromSourceGroups(SourceGroup[] groups) {
        urls = new LinkedList<URL>();
        for (SourceGroup sourceGroup : groups) {
            getUrlsFromSourceGroup(sourceGroup);
        }
    }

    private void addClassLoader() {
        loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        manager.addClassLoader(loader);
        TemplateLoader.setClassLoader(loader);
    }

    private void getUrlsFromSourceGroup(SourceGroup sourceGroup) {
        ClassPath path = ClassPath.getClassPath(sourceGroup.getRootFolder(), ClassPath.EXECUTE);
        if (path != null) {
            getURLsFromPath(path);
        }
    }

    private void getURLsFromPath(ClassPath path) {
        FileObject[] roots = path.getRoots();
        for (FileObject fileObject : roots) {
            addURLfromFileObject(fileObject);
        }
    }

    private void addURLfromFileObject(FileObject fileObject) {
        if (shouldURLbeAdded(fileObject)) {
            urls.add(fileObject.toURL());
        }
    }

    private boolean shouldURLbeAdded(FileObject fileObject) {
        return !fileObject.equals(manager.getAssetFolder()) && !urls.contains(fileObject.toURL());
    }

    private ColorRGBA convertProxyColorToRGBA(String proxyColor) {
        if ("Black".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Black;
        } else if ("White".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.White;
        } else if ("DarkGray".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.DarkGray;
        } else if ("Gray".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Gray;
        } else if ("LightGray".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.LightGray;
        } else if ("Red".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Red;
        } else if ("Green".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Green;
        } else if ("Blue".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Blue;
        } else if ("Yellow".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Yellow;
        } else if ("Magenta".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Magenta;
        } else if ("Cyan".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Cyan;
        } else if ("Orange".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Orange;
        } else if ("Brown".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Brown;
        } else if ("Pink".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.Pink;
        } else if ("BlackNoAlpha".equalsIgnoreCase(proxyColor)) {
            return ColorRGBA.BlackNoAlpha;
        } else {
            return null;
        }
    }
}
