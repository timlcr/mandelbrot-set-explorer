package persistence;

import image.MandelbrotImage;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MandelbrotImageIO {

    private static final JFileChooser fileChooser = new JFileChooser();

    public static void saveImageData(MandelbrotImage image, String filename) {
        MandelbrotImageData data = image.data();
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(data);
        } catch (Exception e) { throw new RuntimeException("Error saving image data", e); }
    }

    public static MandelbrotImage load(String filename) {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            MandelbrotImageData data = (MandelbrotImageData) in.readObject();
            return MandelbrotImage.fromData(data);
        } catch (Exception e) { throw new RuntimeException("Error loading image data", e); }
    }

}
