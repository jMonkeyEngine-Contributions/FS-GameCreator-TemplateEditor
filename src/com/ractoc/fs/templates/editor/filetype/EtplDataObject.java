package com.ractoc.fs.templates.editor.filetype;

import com.jme3.asset.AssetKey;
import com.jme3.export.Savable;
import com.jme3.gde.core.assets.*;
import com.ractoc.fs.parsers.entitytemplate.*;
import com.ractoc.fs.templates.editor.EtplOpenSupport;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.*;
import org.openide.*;
import org.openide.awt.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.NbBundle.Messages;

@Messages({
    "LBL_Etpl_LOADER=Entity Template files"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Etpl_LOADER",
        mimeType = "application/e-tpl",
        extension = {"etpl", "ETPL"})
@DataObject.Registration(
        mimeType = "application/e-tpl",
        iconBase = "com/ractoc/fs/templates/editor/meta/icon.gif",
        displayName = "#LBL_Etpl_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/application/e-tpl/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class EtplDataObject extends AssetDataObject {

    private EntityTemplate template;
    private Project project;
    private ProjectAssetManager manager;
    private URLClassLoader loader;
    private List<URL> urls;
    private String fileName;

    public EtplDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        setupCookies();
    }

    private void setupCookies() {
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) new EtplOpenSupport(this));
        cookies.assign(OpenCookie.class, new EtplOpenSupport(this));
        cookies.assign(CloseCookie.class, new EtplOpenSupport(this));
    }

    private void setupManager() {
        manager = getLookup().lookup(ProjectAssetManager.class);
        if (manager == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
        }
    }

    private void setupProject() {
        project = manager.getProject();
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    public synchronized Savable loadAsset() {
        setupManager();
        setupProject();
        extractFileName();
        setupClassLoader();
        return loadEntityTemplate();
    }

    private void setupClassLoader() {
        SourceGroup[] groups = getSourceGroupFromProject();
        getURLsFromSourceGroups(groups);
        addClassLoader();
    }

    private Savable loadEntityTemplate() {
        template = manager.loadAsset(getAssetKey());
        return template;
    }

    private void resetLoader() {
        if (loader != null) {
            manager.removeClassLoader(loader);
        }
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

    @Override
    public synchronized void saveAsset() throws IOException {
        resetLoader();
        File outputFile = new File(getFileName());
        saveAssetToFile(outputFile);
    }

    private void saveAssetToFile(File outputFile) {
        TemplateWriter writer = new TemplateWriter();
        writer.setLoader(loader);
        writer.write(template, outputFile);
        this.setModified(false);
    }

    @Override
    public synchronized AssetKey<EntityTemplate> getAssetKey() {
        if (assetKeyHasInCorrectType()) {
            assetKey = new TemplateKey(super.getAssetKey().getName());
        }
        return (TemplateKey) assetKey;
    }

    private boolean assetKeyHasInCorrectType() {
        return !(super.getAssetKey() instanceof TemplateKey);
    }

    public void setEntityTemplate(EntityTemplate template) {
        this.template = template;
    }

    public URLClassLoader getClassLoader() {
        return loader;
    }

    private void extractFileName() {
        fileName = getPrimaryFile().getPath();
    }

    public String getFileName() {
        return fileName;
    }

    public EntityTemplate getTemplate() {
        return template;
    }
}
