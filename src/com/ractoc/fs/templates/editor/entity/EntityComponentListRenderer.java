package com.ractoc.fs.templates.editor.entity;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class EntityComponentListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            setText(value.getClass().getSimpleName());
        }
        return comp;
    }

}
