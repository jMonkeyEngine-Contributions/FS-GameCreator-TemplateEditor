/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ractoc.fs.templates.editor;

import com.ractoc.fs.templates.editor.entity.ModificationListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 */
public class FieldModifiedListener implements KeyListener {
    private final ModificationListener listener;

    public FieldModifiedListener(ModificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        listener.setModified(true);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
