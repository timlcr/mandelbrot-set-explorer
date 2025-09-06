package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StudioDisplay extends ImageDisplay {

    private final Studio parent;

    private boolean selectingView = false;
    private Point dragEnd = null;


    public StudioDisplay(Studio parent) {
        this.parent = parent;
    }

    public void setSelectingView(boolean sv) { this.selectingView = sv; }
    public boolean selectingView() { return this.selectingView; }

    @Override
    protected ImagePanel imagePanel() { return new StudioImagePanel(); }

    class StudioImagePanel extends ImagePanel {

        private Point selectedCenterCoords = null;

        public StudioImagePanel() {
            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (selectedCenterCoords != null) {
                int x = selectedCenterCoords.x;
                int y = selectedCenterCoords.y;
                g.setColor(Color.BLACK);
                g.fillOval(x - 10, y - 10, 20, 20);
                g.setColor(Color.RED);
                g.fillOval(x - 8, y - 8, 16, 16);
            }
            if (dragEnd != null) {
                int width = 2 * Math.abs(dragEnd.x - selectedCenterCoords.x);
                int height = width / parent.renderControls.getImageWidth()
                        * parent.renderControls.getImageHeight();
                int x = dragEnd.x;
                int y = dragEnd.y;
                if (x > selectedCenterCoords.x) x -= width;
                if (y > selectedCenterCoords.y) y -= height;
                g.drawRect(x, y, width, height);
                g.drawLine(dragEnd.x, dragEnd.y, selectedCenterCoords.x, selectedCenterCoords.y);
            }
        }

        private MouseAdapter mouseAdapter() {

            return new MouseAdapter() {

                int dragStartX = -1;
                int dragStartY = -1;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectingView && selectedCenterCoords == null) {
                        selectedCenterCoords = new Point(e.getX(), e.getY());
                        parent.renderControls.setCenter(numAt(selectedCenterCoords.x, selectedCenterCoords.y));
                        repaint();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (selectingView && selectedCenterCoords != null) {
                        dragStartX = e.getX();
                        dragStartY = e.getY();
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectingView && dragStartX >= 0) {
                        dragEnd = new Point(e.getX(), e.getY());
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (selectingView && dragEnd != null) {
                        double zoom = Math.abs(
                                numAt(e.getX(), e.getY()).imaginary()
                                        - numAt(dragStartX, dragStartY).imaginary()
                        );
                        parent.renderControls.setZoomLevel(zoom);
                        selectingView = false;
                        selectedCenterCoords = null;
                        parent.handleCancelSelectView();
                        repaint();
                    }
                    dragStartX = -1;
                    dragStartY = -1;
                    dragEnd = null;
                }

            };
        }

    }
}
