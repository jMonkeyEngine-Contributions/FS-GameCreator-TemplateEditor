package com.ractoc.fs.templates.editor.entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

public class RemoveComponentListener implements ActionListener {

    private JList componentList;

    public RemoveComponentListener(JList componentList) {
        this.componentList = componentList;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int index = componentList.getSelectedIndex();
        if (index >= 0) {
            componentList.remove(index);
        }
    }
}
