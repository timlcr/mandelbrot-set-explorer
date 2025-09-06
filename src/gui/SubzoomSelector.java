package gui;

import util.Complex;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class SubzoomSelector extends MouseAdapter {

    private final ImageDisplay.ImagePanel display;
    private final BiConsumer<Complex, Double> handleZoom;

    private boolean selecting = false;

    private int pressX = 0;
    private int pressY = 0;

    public SubzoomSelector(ImageDisplay.ImagePanel display, BiConsumer<Complex, Double> handleZoom) {
        this.display = display;
        this.handleZoom = handleZoom;
        display.addMouseListener(this);
        display.addMouseMotionListener(this);
    }

    public boolean isSelecting() { return selecting; }

    @Override
    public void mousePressed(MouseEvent e) {
        selecting = true;
        pressX = e.getX();
        pressY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int rectWidth = 2 * Math.abs(e.getX() - pressX);
        int rectHeight = 2 * Math.abs(e.getY() - pressY);
        int rectX = e.getX() > pressX ? e.getX() - rectWidth : e.getX();
        int rectY = e.getY() > pressY ? e.getY() - rectHeight : e.getY();
        display.setSelectionRect(rectX, rectY, rectWidth, rectHeight);
        display.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selecting = false;
        display.setSelectionRect(0, 0, 0, 0);
        display.repaint();
        if (Math.abs(pressY - e.getY()) < 10) return;
        Complex center = display.numAt(pressX, pressY);
        Complex corner = display.numAt(e.getX(), e.getY());
        double zoom = 2 * Math.abs(corner.imaginary() - center.imaginary());
        handleZoom.accept(center, zoom);
    }
}
