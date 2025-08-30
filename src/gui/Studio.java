package gui;

import image.MandelbrotImage;
import persistence.MandelbrotImageIO;
import util.Complex;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * A component with the ability to create MandelbrotImages, display them, and load and save them.
 */
public class Studio extends JPanel {

    private final ImageDisplay imageDisplay = new ImageDisplay();
    private final RenderControls renderControls = new RenderControls(this::handleRender, null);
    private final ColorControls colorControls = new ColorControls(this::handleRecolor, null);

    private final JFileChooser fileChooser = new JFileChooser();
    private final JProgressBar progressBar = new JProgressBar();

    /**
     * Constructs a Workspace
     */
    public Studio() {
        super(new BorderLayout());

        imageDisplay.setBorder(GUI.imageDisplayBorder());
        add(imageDisplay, BorderLayout.CENTER);

        add(bottomPanel(), BorderLayout.SOUTH);
    }

    /**
     * Saves the MandelbrotImage data to disc
     */
    public void handleSave() {
        MandelbrotImage image = image();
        if (image == null) return;
        String saveName = JOptionPane.showInputDialog("Enter image name");
        String dataFilename = "storage/imagedata/" + saveName + ".dat";
        MandelbrotImageIO.saveImageData(image, dataFilename);
    }

    /**
     * Loads a MandelbrotImage from a data file selected by the user.
     */
    public void handleLoad() {
        File dataDir = new File("storage/imagedata");
        if (!dataDir.exists()) { dataDir.mkdirs(); }
        fileChooser.setCurrentDirectory(dataDir);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            MandelbrotImage image = MandelbrotImageIO.load(fileChooser.getSelectedFile());
            setImage(image);
        }
    }

    /**
     * Exports the image being displayed as a png to a location selected by the user.
     */
    public void handleExport() {
        MandelbrotImage image = image();
        if (image == null) return;
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getParentFile(), file.getName() + ".png");
                }
                ImageIO.write(image, "png", file);
            } catch (IOException e) { throw new RuntimeException("Image export error", e); }
        }
    }

    /**
     * Returns the image being displayed.
     * @return the image being displayed
     */
    public MandelbrotImage image() { return imageDisplay.image(); }

    public void renderControlsVisible(boolean visible) {
        renderControls.setVisible(visible);
    }

    public void colorControlsVisible(boolean visible) {
        colorControls.setVisible(visible);
    }

    private void setImage(MandelbrotImage image) {
        imageDisplay.setImage(image);
        renderControls.setImage(image);
        colorControls.setImage(image);
    }

    private void handleRender() {
        Complex center = new Complex(renderControls.getCenterReal(), renderControls.getCenterImaginary());
        double zoom = renderControls.getZoom();
        Consumer<Double> progressObserver = p -> progressBar.setValue((int) (p * 100));
        SwingWorker<MandelbrotImage, Void> worker = new SwingWorker<>() {
            @Override
            protected MandelbrotImage doInBackground() throws Exception {
                return new MandelbrotImage(
                        renderControls.getImageWidth(), renderControls.getImageHeight(),
                        center, zoom, renderControls.getMaxN(),
                        colorControls.getColorFunction(), colorControls.getColorFuncParams(zoom),
                        progressObserver
                );
            }
            @Override
            protected void done() {
                try {
                    imageDisplay.setImage(get());
                } catch (Exception ignore) {}
            }
        };
        worker.execute();
    }

    private void handleRecolor() {
        MandelbrotImage image = image();
        if (image == null) return;
        image.setColorFunction(colorControls.getColorFunction());
        image.setColorFuncParams(colorControls.getColorFuncParams(image.zoom));
        image.colorImage();
        imageDisplay.repaint();
    }

    void setZoomCoords(Complex center, double zoom) {
        renderControls.setZoomCoords(center, zoom);
    }

    private JPanel bottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(buttonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buttonPanel() {
        JButton renderButton = new JButton("Render");
        JButton colorButton = new JButton("Colour");
        JButton saveButton = new JButton("Save project");
        JButton loadButton = new JButton("Load project");
        JButton exportButton = new JButton("Export image");
        renderButton.addActionListener(e -> renderControls.setVisible(true));
        colorButton.addActionListener(e -> colorControls.setVisible(true));
        saveButton.addActionListener(e -> handleSave());
        loadButton.addActionListener(e -> handleLoad());
        exportButton.addActionListener(e -> handleExport());
        JPanel panel = new JPanel();
        panel.add(renderButton);
        panel.add(colorButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(exportButton);
        return panel;
    }

}
