package com.ractoc.fs.templates.editor;

import com.jme3.asset.AssetKey;
import com.jme3.gde.core.assets.AssetDataObject;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;
import com.ractoc.fs.templates.editor.entity.EntityTemplateEditorPanel;
import com.ractoc.fs.templates.editor.filetype.EtplDataObject;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

@ConvertAsProperties(
    dtd = "-//com.ractoc.fs//entityTemplate//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "EtplTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_EtplAction=Etpl",
    "CTL_EtplTopComponent=Etpl Window",
    "HINT_EtplTopComponent=Entity Template editor window."
})
public final class EtplTopComponent extends CloneableTopComponent {
    public static final String PROP_FILENAME = "filename";

    private EtplDataObject dataObject;
    private EntityTemplate template;

    public EtplTopComponent() {
        super();
        initComponents();
        setName(Bundle.CTL_EtplTopComponent());
        setToolTipText(Bundle.HINT_EtplTopComponent());
    }

    public void setEtplDataObject(EtplDataObject edo) {
        this.dataObject = edo;
        this.setActivatedNodes(new Node[]{edo.getNodeDelegate()});
        loadTemplate();
        setupEditorPanel();
    }

    private void loadTemplate() {
        dataObject.loadAsset();
        template = dataObject.getTemplate();
    }

    private void setupEditorPanel() {
        setDisplayName(template.getName());
        EntityTemplateEditorPanel etep = new EntityTemplateEditorPanel(this);
        etep.editTemplate(template);
        setEditorPanel(etep);
    }

    private void setEditorPanel(JPanel editorPanel) {
        this.add(editorPanel);
        this.invalidate();
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public EtplDataObject getDataObject() {
        return dataObject;
    }

    public void displayErrorMessage(String errorMessage) {
        new JDialog((Frame) null, errorMessage, true);
    }

    public void setModified(boolean modif) {
        dataObject.setModified(modif);
    }

    public URLClassLoader getLoader() {
        return dataObject.getClassLoader();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    void writeProperties(java.util.Properties p) throws IOException {
        p.setProperty(PROP_FILENAME, FileUtil.toFile(dataObject.getPrimaryFile()).getCanonicalPath());
    }

    void readProperties(java.util.Properties p) throws IOException {
        String fileName = p.getProperty(PROP_FILENAME);
        loadTemplateForFileName(fileName);
    }

    private void loadTemplateForFileName(String fileName) throws IOException, DataObjectNotFoundException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new IOException("No such file: " + f);
        }
        f = FileUtil.normalizeFile(f);
        loadTemplateForFile(f);
    }

    private void loadTemplateForFile(File f) throws DataObjectNotFoundException {
        AssetDataObject assetObject = createAssetDataObjectForFile(f);
        assetObject.loadAsset();
        setEtplDataObject((EtplDataObject) assetObject);
    }

    private AssetDataObject createAssetDataObjectForFile(File f) throws DataObjectNotFoundException {
        FileObject fObject = FileUtil.toFileObject(f);
        AssetDataObject assetObject = (AssetDataObject) DataObject.find(fObject);
        AssetKey aKey = new AssetKey(f.getName());
        assetObject.setAssetKeyData(aKey);
        return assetObject;
    }
}
