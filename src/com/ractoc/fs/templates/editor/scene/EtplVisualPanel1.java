/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ractoc.fs.templates.editor.scene;

import com.jme3.gde.core.assets.ProjectAssetManager;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

public final class EtplVisualPanel1 extends JPanel {

    private Map<String, FileObject> etplFiles = new HashMap<String, FileObject>();
    private static final Logger logger = Logger.getLogger(EtplVisualPanel1.class.getName());
    private final Node node;
    private Project project;
    private ProjectAssetManager manager;

    public EtplVisualPanel1(Node node) {
        this.node = node;
        setupManager();
        setupProject();
        initComponents();
        FileObject assetFolder = manager.getAssetFolder();
        getTemplateFiles(assetFolder);
//        populateCombo();
//        this.invalidate();
//        this.validate();
    }

    private void setupManager() {
        manager = node.getLookup().lookup(ProjectAssetManager.class);
        if (manager == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
        }
    }

    private void setupProject() {
        project = manager.getProject();
    }

    private void getTemplateFiles(FileObject fileObject) {
        if (fileObject.isFolder()) {
            for (FileObject childObject : fileObject.getChildren()) {
                getTemplateFiles(childObject);
            }
        } else {
            if (fileObject.getExt().equalsIgnoreCase("etpl")) {
                etplFiles.put(fileObject.getName(), fileObject);
                entityTemplateCombo.addItem(fileObject.getName());
            }
        }
    }

    @Override
    public String getName() {
        return "Template File";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jLabel1 = new javax.swing.JLabel();
        entityTemplateCombo = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(150);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EtplVisualPanel1.class, "EtplVisualPanel1.jLabel1.text")); // NOI18N
        jSplitPane1.setLeftComponent(jLabel1);

        jSplitPane1.setRightComponent(entityTemplateCombo);

        add(jSplitPane1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox entityTemplateCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    private void populateCombo() {
        String[] etplNames = etplFiles.keySet().toArray(new String[]{});
        entityTemplateCombo = new JComboBox(etplNames);
        entityTemplateCombo.setName("entityTemplateList");
        entityTemplateCombo.setEditable(false);
    }

    public FileObject getTemplateFile() {
        if (entityTemplateCombo.getSelectedItem() != null) {
            String entityName = (String) entityTemplateCombo.getSelectedItem();
            return etplFiles.get(entityName);
        } else {
            return null;
        }
    }
}
