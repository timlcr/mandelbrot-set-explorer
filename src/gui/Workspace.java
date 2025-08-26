package gui;

import image.MandelbrotImage;
import persistence.MandelbrotImageIO;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

/**
 * A component with the ability to create MandelbrotImages, display them, and load and save them.
 */
public class Workspace extends JPanel {

    private final ImageDisplay imageDisplay = new ImageDisplay();

    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Constructs a Workspace
     */
    public Workspace() {
        super(new BorderLayout());

        imageDisplay.setBorder(BorderFactory.createLoweredBevelBorder());
        add(imageDisplay, BorderLayout.CENTER);

        add(savePanel(), BorderLayout.SOUTH);
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
            imageDisplay.setImage(image);
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

    private JPanel savePanel() {
        JButton saveButton = new JButton("Save project");
        JButton loadButton = new JButton("Load project");
        JButton exportButton = new JButton("Export image");
        saveButton.addActionListener(e -> handleSave());
        loadButton.addActionListener(e -> handleLoad());
        exportButton.addActionListener(e -> handleExport());
        JPanel panel = new JPanel();
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(exportButton);
        return panel;
    }
}
