package com.ractoc.fs.templates.editor.filetype;

import com.jme3.asset.AssetManager;
import com.jme3.gde.core.assets.AssetManagerConfigurator;
import com.ractoc.fs.parsers.entitytemplate.TemplateLoader;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AssetManagerConfigurator.class)
public class EtplAssetManagerConfigurator implements AssetManagerConfigurator {

    @Override
    public void prepareManager(AssetManager manager) {
        manager.registerLoader(TemplateLoader.class, "etpl", "ETPL");
    }
}
