package persistence;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for managing the persistent storage directory for the Mandelbrot Set Explorer application.
 * <p>
 * This class ensures that a root directory is created in the user's home folder (by default
 * {@code ~/Mandelbrot Set Explorer/}), along with a subdirectory for storing data files
 * ({@code storage/imagedata/}). It also handles copying default resources from the application
 * JAR into the appropriate directories, including example files and a README, without overwriting
 * any existing user files.
 * </p>
 * <p>
 * This allows the application to maintain a consistent, writable storage location for user data,
 * while keeping bundled default resources accessible on first run.
 * </p>
 */
public class StorageHelper {
    // Root directory under user's home
    private static final File ROOT_DIR = new File(System.getProperty("user.home"), "MandelbrotSetExplorer");
    private static final File IMAGE_DIR = new File(ROOT_DIR, "storage/imagedata");

    /**
     * Ensures that the storage directories exist and copies any default resources from the JAR.
     * <p>
     * This method will:
     * <ul>
     *     <li>Create the root directory under the user's home directory (e.g., {@code ~/Mandelbrot Set Explorer/}).</li>
     *     <li>Create the subdirectory {@code storage/imagedata/} for persistent data.</li>
     *     <li>Copy the README file into the root directory if it does not already exist.</li>
     *     <li>Copy any default files listed in {@code defaults/defaults.txt} into the {@code storage/imagedata/} directory.</li>
     * </ul>
     * Existing user files will not be overwritten.
     *
     * @return the {@link File} object representing the {@code storage/imagedata/} directory.
     */
    public static File getStorageDirectory() {
        // Ensure root and image directories exist
        if (!IMAGE_DIR.exists() && !IMAGE_DIR.mkdirs()) {
            System.err.println("Warning: Could not create storage directory at " + IMAGE_DIR.getAbsolutePath());
        }

        // Always try to copy README into the root folder
        copyDefaultResource("defaults/README.txt", new File(ROOT_DIR, "README.txt"));

        // Load defaults manifest and copy listed files into imagedata/
        try (InputStream in = StorageHelper.class.getClassLoader().getResourceAsStream("defaults/defaults.txt")) {
            if (in == null) {
                System.err.println("No defaults.txt found in resources");
                return IMAGE_DIR;
            }

            List<String> files = new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#")) // allow comments
                    .collect(Collectors.toList());

            for (String filename : files) {
                copyDefaultResource("defaults/" + filename, new File(IMAGE_DIR, filename));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return IMAGE_DIR;
    }

    /**
     * Copies a default resource from the application JAR to a target file on the filesystem.
     * <p>
     * This method will not overwrite the target file if it already exists. It ensures that
     * any parent directories are created before copying the file.
     * </p>
     *
     * @param resourcePath the path of the resource inside the JAR (e.g., {@code "defaults/example.png"}).
     * @param targetFile   the {@link File} object representing the destination file on disk.
     */
    private static void copyDefaultResource(String resourcePath, File targetFile) {
        if (targetFile.exists()) {
            return; // Don’t overwrite user files
        }
        try (InputStream in = StorageHelper.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("Resource not found in JAR: " + resourcePath);
                return;
            }
            Files.createDirectories(targetFile.getParentFile().toPath()); // ensure parent exists
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied default resource: " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

