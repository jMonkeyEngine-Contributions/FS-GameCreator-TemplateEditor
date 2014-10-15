package com.ractoc.fs.templates.editor;

import com.jme3.gde.core.assets.AssetData;
import com.ractoc.fs.templates.editor.filetype.EtplDataObject;
import org.openide.cookies.*;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

public class EtplOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    private EtplDataObject dataObject;
    private AssetData assetData;
    private EtplTopComponent topComponent;

    public EtplOpenSupport(EtplDataObject etpl) {
        super(etpl.getPrimaryEntry());
        dataObject = etpl;
        Lookup lookup = dataObject.getLookup();
        assetData = lookup.lookup(AssetData.class);
    }

    @Override
    public void open() {
        super.open();
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
