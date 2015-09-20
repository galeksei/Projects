import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class FileManip {
    String name;

    public FileManip(String loc) {
        this.name = loc;
    }

    /**
     * Copies the file to a destination
     * 
     * @param dest
     *            destination path
     */
    public void copyFile(String dest) {
        File origin = new File(name);

        Path from = origin.toPath();
        File toFile = new File(dest);
        toFile.mkdirs();

        Path to = toFile.toPath();
        // overwrite existing file, if exists
        CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING };
        try {
            Files.copy(from, to, options);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Create a text file
     * 
     * @param fileName
     * @param fileText
     */
    public void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
    }

    /**
     * Replaces all text in the existing file with the given text.
     * 
     * @param fileName
     * @param fileText
     */
    public void writeFile(String fileName, String fileText) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, false);
            fw.write(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes a file
     */
    public void delete() {
        Path fileLoc = Paths.get(name);
        try {
            Files.deleteIfExists(fileLoc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * checks if the 2 files have the same content
     * 
     * @param f1
     * @param f2
     * @return
     * @throws IOException
     */
    public boolean isSame(String f2) {
        byte[] b1 = null;
        try {
            b1 = Files.readAllBytes(Paths.get(name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] b2 = null;
        try {
            b2 = Files.readAllBytes(Paths.get(f2));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i += 1) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a directory
     * 
     * @param dirName
     */
    public void makeDir() {
        File dir = new File(name);
        dir.mkdir();
    }

    /**
     * checks whether the file exists
     * 
     * @return
     */
    public boolean exists() {
        File file = new File(name);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Gets the location of the given file
     * 
     * @return
     */
    public String getPath() {
        return name;
    }
}
