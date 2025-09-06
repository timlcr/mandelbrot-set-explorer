package persistence;

import algorithm.RepresentationValue;
import image.ColorFunctionParameters;
import image.ColorFunctionType;
import image.Gradient;
import image.MandelbrotImage;
import util.Complex;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Contains methods for saving/loading MandelbrotImages to/from disc.
 */
public class MandelbrotImageIO {

    /**
     * Saves all necessary data to allow a MandelbrotImage to be loaded into
     * the program later and altered further.
     * @param image the MandelbrotImage being saved
     * @param filename the name of the file the data is saved to
     */
    public static void saveImageData(MandelbrotImage image, String filename) {
        MandelbrotImageData data = image.data();
        try (FileOutputStream fos = new FileOutputStream(filename);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GZIPOutputStream gos = new GZIPOutputStream(bos);
             DataOutputStream dos = new DataOutputStream(gos);
             ObjectOutputStream oos = new ObjectOutputStream(gos)) { // only for gradient

            dos.writeInt(data.width());
            dos.writeInt(data.height());
            dos.writeDouble(data.center().real());
            dos.writeDouble(data.center().imaginary());
            dos.writeDouble(data.zoom());
            dos.writeInt(data.maxN());

            dos.writeInt(data.colorFunction().ordinal());

            oos.writeObject(data.colorFuncParams().gradient());
            dos.writeDouble(data.colorFuncParams().flux());
            dos.writeBoolean(data.colorFuncParams().renderDistEst());
            dos.writeDouble(data.colorFuncParams().maxDistRendered());

            RepresentationValue[][] array = data.array();
            for(int x = 0; x < data.width(); x++) {
                for(int y = 0; y < data.height(); y++) {
                    RepresentationValue val = array[x][y];
                    dos.writeDouble(val.lastZ().real());
                    dos.writeDouble(val.lastZ().imaginary());
                    dos.writeInt(val.escapeIter());
                    dos.writeDouble(val.distEst());
                    dos.writeBoolean(val.orbitFound());
                }
            }
        } catch (Exception e) { throw new RuntimeException("Error saving image data", e); }
    }

    /**
     * Loads a MandelbrotImage from a data file.
     * @param file the data file
     * @return a MandelbrotImage specified by the data in <code>file</code>
     */
    public static MandelbrotImage load(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             GZIPInputStream gis = new GZIPInputStream(bis);
             DataInputStream dis = new DataInputStream(gis);
             ObjectInputStream ois = new ObjectInputStream(gis)) {

            int width = dis.readInt();
            int height = dis.readInt();
            double centerReal = dis.readDouble();
            double centerImag = dis.readDouble();
            Complex center = new Complex(centerReal, centerImag);
            double zoom = dis.readDouble();
            int maxN = dis.readInt();

            int enumOrdinal = dis.readInt();
            ColorFunctionType colorFunc = ColorFunctionType.values()[enumOrdinal];

            Gradient gradient = (Gradient) ois.readObject();
            double flux = dis.readDouble();
            boolean renderDistEst = dis.readBoolean();
            double maxDistRendered = dis.readDouble();
            ColorFunctionParameters colorFuncParams = new ColorFunctionParameters(gradient, flux, renderDistEst, maxDistRendered);

            RepresentationValue[][] array = new RepresentationValue[width][height];
            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    double real = dis.readDouble();
                    double imag = dis.readDouble();
                    int escapeIter = dis.readInt();
                    double distEst = dis.readDouble();
                    boolean orbitFound = dis.readBoolean();
                    array[x][y] = new RepresentationValue(
                            new Complex(real, imag), escapeIter, distEst, orbitFound
                    );
                }
            }
            MandelbrotImageData imageData = new MandelbrotImageData(
                    width, height, center, zoom, maxN, array, colorFunc, colorFuncParams
            );
            return MandelbrotImage.fromData(imageData);
        } catch (Exception e) { throw new RuntimeException("Error loading image data", e); }
    }

    /*public static void writeGradient(Gradient gradient, DataOutputStream dos) throws IOException {
        dos.writeInt(gradient.name.length());
        dos.writeChars(gradient.name);
        if (gradient.colorStops != null) {
            dos.writeBoolean(true);
            writeColorStopGradient(gradient, dos);
        } else {
            dos.writeBoolean(false);
            writeWholeGradient(gradient, dos);
        }
    }

    public static Gradient readGradient(DataInputStream dis) throws IOException {
        StringBuilder name = new StringBuilder();
        int nameLength = dis.readInt();
        for (int i = 0; i < nameLength; i++) {
            name.append(dis.readChar());
        }
        boolean colorStops = dis.readBoolean();
        if (colorStops) return readColorStopsGradient(dis);
        else return readWholeGradient(dis);
    }

    private static void writeColorStopGradient(Gradient gradient, DataOutputStream dos) throws IOException{
        if (gradient.colorStops == null) throw new IllegalArgumentException("Incompatible gradient");
        dos.writeInt(gradient.colorStops.size());
        for (Gradient.ColorStop s : gradient.colorStops) {
            writeColor(s.color(), dos);
            dos.writeFloat(s.position());
        }
    }

    private static Gradient readColorStopsGradient(String name, DataInputStream dis) throws IOException {
        List<Gradient.ColorStop> stops = new ArrayList<>();
        int n = dis.readInt();
        for (int i = 0; i < n; i++) {
            Color c = readColor(dis);
            float f = dis.readFloat();
            stops.add(new Gradient.ColorStop(c, f));
        }
        return new Gradient(name, stops, )
    }

    private static void writeWholeGradient(Gradient gradient, DataOutputStream dos) throws IOException {
        dos.writeInt(gradient.size());
        for (int i = 0; i < gradient.size(); i++) {
            writeColor(gradient.get(i), dos);
        }
    }

    private static void writeColor(Color color, DataOutputStream dos) throws IOException {
            int rgb = color.getRGB();
            dos.writeInt(rgb);
    }

    private static Color readColor(DataInputStream dis) throws IOException {
        return new Color(dis.readInt());
    }*/

}
