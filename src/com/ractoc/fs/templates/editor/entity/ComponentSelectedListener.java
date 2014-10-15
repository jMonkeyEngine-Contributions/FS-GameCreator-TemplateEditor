package com.ractoc.fs.templates.editor.entity;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;
import java.awt.Dimension;
import java.lang.reflect.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;

public class ComponentSelectedListener implements ListSelectionListener {

    private EntityComponent component;
    private JList componentList;
    private EntityTemplateEditorPanel editor;

    public ComponentSelectedListener(EntityTemplateEditorPanel editor) {
        this.editor = editor;
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        if (isValidEvent(event)) {
            getSelectedComponentClass(event);
            editor.showComponentEditor(component);
        }
    }

    private boolean isValidEvent(ListSelectionEvent event) {
        return !event.getValueIsAdjusting();
    }

    private void getSelectedComponentClass(ListSelectionEvent event) {
        componentList = (JList) event.getSource();
        component = (EntityComponent) componentList.getSelectedValue();
    }
}
