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

public class Workspace extends JPanel {

    private final ImageDisplay imageDisplay = new ImageDisplay();

    private final JFileChooser fileChooser = new JFileChooser();

    public Workspace() {
        super(new BorderLayout());

        imageDisplay.setBorder(BorderFactory.createLoweredBevelBorder());
        add(imageDisplay, BorderLayout.CENTER);

        add(savePanel(), BorderLayout.SOUTH);
    }

    public void handleSave() {
        MandelbrotImage image = image();
        if (image == null) return;
        String saveName = JOptionPane.showInputDialog("Enter image name");
        String dataFilename = "storage/imagedata/" + saveName + ".dat";
        MandelbrotImageIO.saveImageData(image, dataFilename);
    }

    public void handleLoad() {
        File dataDir = new File("storage/imagedata");
        if (!dataDir.exists()) { dataDir.mkdirs(); }
        fileChooser.setCurrentDirectory(dataDir);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            MandelbrotImage image = MandelbrotImageIO.load(fileChooser.getSelectedFile());
            imageDisplay.setImage(image);
        }
    }

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
