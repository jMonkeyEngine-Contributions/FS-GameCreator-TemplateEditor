package com.ractoc.fs.templates.editor;

import com.jme3.gde.core.assets.AssetData;
import com.ractoc.fs.templates.editor.filetype.EtplDataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.cookies.*;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

public class EtplOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    private static final Logger logger = Logger.getLogger(EtplOpenSupport.class.getName());
    private EtplDataObject dataObject;
    private AssetData assetData;
    private EtplTopComponent topComponent;

    public EtplOpenSupport(EtplDataObject etpl) {
        super(etpl.getPrimaryEntry());
        dataObject = etpl;
        logger.info("EtplOpenSupport: looking up asset");
        Lookup lookup = dataObject.getLookup();
        logger.log(Level.INFO, "EtplOpenSupport: looked up asset {0}", lookup);
        assetData = lookup.lookup(AssetData.class);
    }

    @Override
    public void open() {
        super.open();
        logger.log(Level.INFO, "EtplOpenSupport: opening asset {0}", assetData);
        assetData.loadAsset();
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        if (topComponent == null) {
            topComponent = new EtplTopComponent();
            topComponent.setEtplDataObject(dataObject);
        }
        return topComponent;
    }
}
