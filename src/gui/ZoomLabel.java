package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Displays the location of a MandelbrotImage by displaying the complex number which the image is
 * centered on, and the zoom level. The displayed text can be copied to the clipboard by
 * clicking the ZoomLabel.
 */
public class ZoomLabel extends JLabel {

    private Complex center;
    private double zoom;

    /**
     * Constructs a ZoomLabel and displays the zoom info for the specified image.
     * If the image is null the label is left empty.
     * @param image the <code>MandelbrotImage</code> from which the zoom info is to be displayed
     */
    public ZoomLabel(MandelbrotImage image) {
        addMouseListener(mouseListener());
        if (image != null) update(image);
    }

    /**
     * Updates the text on this ZoomLabel to show the zoom info for the specified image.
     * @param image the image from which the zoom info is to be displayed
     */
    public void update(MandelbrotImage image) {
        if (image == null) {
            setText("");
            return;
        }
        center = image.center;
        zoom = image.zoom;
        setText(center, zoom);
    }

    private void setText(Complex center, double zoom) {
        String s = center.real() + " ";
        if (center.imaginary() > 0) s += "+";
        s += center.imaginary() + "i @ " + String.format("%.2e", zoom);
        setText(s);
    }

    public Complex getCenter() { return center; }
    public double getZoom() { return zoom; }

    private MouseAdapter mouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String zoomText = getText();
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(zoomText), null);
                setText("Copied!");
                Timer timer = new Timer(500, _ -> setText(zoomText));
                timer.setRepeats(false);
                timer.start();
            }
        };
    }
}
