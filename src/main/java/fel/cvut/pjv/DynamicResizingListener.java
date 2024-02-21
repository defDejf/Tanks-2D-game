package fel.cvut.pjv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Dynamically resizes text with the resizing of a specified JPanel.
 */
public class DynamicResizingListener implements ComponentListener {
    JComponent[] toUpdate;
    JPanel resized;
    JPanel toRedraw;
    int fontDivisor;

    /**
     * DynamicResizingListener constructor.
     *
     * @param toUpdate    list of components to set new font size
     * @param resized     panel on which resizing is based on
     * @param toRedraw    panel which may need to get revalidated due to new component sizes
     * @param fontDivisor sets the size of font - higher number means smaller text
     */
    public DynamicResizingListener(JComponent[] toUpdate, JPanel resized, JPanel toRedraw, int fontDivisor) {
        this.toUpdate = toUpdate;
        this.resized = resized;
        this.toRedraw = toRedraw;
        this.fontDivisor = fontDivisor;
    }

    /**
     * Sets new font size calculated from new width and height of resized window.
     * Calls revalidate in case layout changed.
     * Fontsize is calculated by (width + height) / fontDivisor.
     */
    @Override
    public void componentResized(ComponentEvent componentEvent) {
        int width = resized.getWidth();
        int height = resized.getHeight();
        Font f = new Font(Font.SERIF, Font.BOLD, (width + height) / fontDivisor);
        for (Component c : toUpdate) {
            c.setFont(f);
        }
        toRedraw.revalidate();
    }

    /**
     * Empty method - Do not use.
     */
    @Override
    public void componentMoved(ComponentEvent componentEvent) {
    }

    /**
     * Empty method - Do not use.
     */
    @Override
    public void componentShown(ComponentEvent componentEvent) {
    }

    /**
     * Empty method - Do not use.
     */
    @Override
    public void componentHidden(ComponentEvent componentEvent) {
    }
}
