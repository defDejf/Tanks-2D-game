package fel.cvut.pjv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class SidePanelsDynamicResizer implements ComponentListener {
    JFrame resized;
    JPanel sidePanel1;
    JPanel sidePanel2;
    Dimension squareDim;

    protected SidePanelsDynamicResizer(JFrame resized, JPanel sidePanel1, JPanel sidePanel2, Dimension squareDim) {
        this.resized = resized;
        this.sidePanel1 = sidePanel1;
        this.sidePanel2 = sidePanel2;
        this.squareDim = squareDim;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        int width = (int) (resized.getWidth() - squareDim.getWidth()) / 2;
        int height = resized.getHeight();
        sidePanel1.setPreferredSize(new Dimension(width, height));
        sidePanel2.setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
}
