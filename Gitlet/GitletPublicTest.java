import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that provides JUnit tests for Gitlet, as well as a couple of utility
 * methods.
 * 
 * @author Joseph Moghadam
 * 
 *         Some code adapted from StackOverflow:
 * 
 *         http://stackoverflow.com/questions
 *         /779519/delete-files-recursively-in-java
 * 
 *         http://stackoverflow.com/questions/326390/how-to-create-a-java-string
 *         -from-the-contents-of-a-file
 * 
 *         http://stackoverflow.com/questions/1119385/junit-test-for-system-out-
 *         println
 * 
 */
public class GitletPublicTest {
    private static final String GITLET_DIR = ".gitlet/";
    private static final String TESTING_DIR = "test_files/";

    /* matches either unix/mac or windows line separators */
    private static final String LINE_SEPARATOR = "\r\n|[\r\n]";

    /**
     * Deletes existing gitlet system, resets the folder that stores files used
     * in testing.
     * 
     * This method runs before every @Test method. This is important to enforce
     * that all tests are independent and do not interact with one another.
     */
    @Before
    public void setUp() {
        File f = new File(GITLET_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f = new File(TESTING_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f.mkdirs();
    }

    /**
     * Tests that init creates a .gitlet directory. Does NOT test that init
     * creates an initial commit, which is the other functionality of init.
     */

    @Test
    public void testBasicInitialize() {
        gitlet("init");
        File f = new File(GITLET_DIR);
        assertTrue(f.exists());
    }
    /**
     * checks if the directory already exists
     */
    @Test
    public void testBasicInitializeFail() {
        gitlet("init");
        String error = gitlet("init");
        error = extractError(error);
        String compare = "A gitlet version control system already exists in the current directory.";
        assertEquals(compare, error);
    }
    
    /**
     * tests failure cases for add functionality
     */
    @Test
    public void testFileAddFailures(){
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        gitlet("init");
        String error1 =gitlet("add", wugFileName);
        error1 = extractError(error1);
        String errorMsg = "File does not exist.";
        assertEquals(error1, errorMsg);
        createFile(wugFileName, wugText);
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        error1 =extractError(gitlet("add", wugFileName));
        errorMsg = "File has not been modified since the last commit.";
        assertEquals(errorMsg, error1);
    }
    
    
    /**
     * Tests that checking out a file name will restore the version of the file
     * from the previous commit. Involves init, add, commit, and checkout.
     * 
     * @throws IOException
     */
    @Test
    public void testBasicCheckout() throws IOException {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);

        gitlet("init");

        gitlet("add", wugFileName);

        gitlet("commit", "added wug");

        writeFile(wugFileName, "This is not a wug.");

        gitlet("checkout", wugFileName);

        assertEquals(wugText, getText(wugFileName));
    }
    /**
     * 
     */
    @Test
    public void testReset() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");

        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added 2");
        gitlet("reset", "1");
        assertEquals(wugText, getText(wugFileName));
    }

    /**
     * Tests checkout by Id
     */
    @Test
    public void testBasicCheckoutWithId() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added 2");
        gitlet("checkout", "1", wugFileName);
        assertEquals(wugText, getText(wugFileName));
    }
    
    /**
     * Tests the case when current branch has the same file as the merge branch
     * and the given branch has a different file
     */
    @Test
    public void testBasicMerge() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added 2");
        gitlet("branch", "new");
        gitlet("checkout", "new");
        String wugFileName2 = TESTING_DIR + "wug2.txt";
        String wugText2 = "This is a wug 2.";
        createFile(wugFileName2, wugText2);
        gitlet("add", wugFileName2);
        writeFile(wugFileName, "This is not a wug blarp.");
        gitlet("add", wugFileName);
        gitlet("commit", "added modified wug");
        gitlet("checkout", "master");
        gitlet("merge", "new");
        assertEquals("This is not a wug blarp.", getText(wugFileName));
    }

    @Test
    public void testBasicRebase() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        gitlet("branch", "new");
        gitlet("checkout", "new");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added 2");
        gitlet("checkout", "master");
        gitlet("rebase", "new");
        assertEquals("This is not a wug.", getText(wugFileName));
    }

    /**
     * Tests the case where both of the files have been modified and different
     * from the split branch
     */
    @Test
    public void testBasicMergeDifferent() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added 2");
        gitlet("branch", "new");
        gitlet("checkout", "new");
        String wugFileName2 = TESTING_DIR + "wug2.txt";
        String wugText2 = "This is a wug 2.";
        createFile(wugFileName2, wugText2);
        gitlet("add", wugFileName2);
        writeFile(wugFileName, "This is not a wug blarp.");
        gitlet("add", wugFileName);
        gitlet("commit", "added modified wug");
        gitlet("checkout", "master");
        writeFile(wugFileName, "This is not a wug, but who cares.");
        gitlet("add", wugFileName);
        gitlet("commit", "added different wug");
        gitlet("merge", "new");
        File testingFile = new File(wugFileName + ".conflicted");
        assertTrue(testingFile.exists());
    }

    /**
     * Test the checkout branch functionality
     * 
     * @throws IOException
     */
    @Test
    public void testCheckoutBranch() throws IOException {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);

        gitlet("init");

        gitlet("add", wugFileName);

        gitlet("commit", "added wug");

        gitlet("branch", "new");

        writeFile(wugFileName, "This is not a wug.");

        gitlet("add", wugFileName);

        gitlet("commit", "added 2");

        gitlet("checkout", "new");

        assertEquals(wugText, getText(wugFileName));
    }

    /**
     * Tests duplicate methods
     */
    @Test
    public void testDuplicate() {
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        createFile(TESTING_DIR + "wug2.txt", wugText);
        FileManip test = new FileManip(wugFileName);
        assertTrue(test.isSame(TESTING_DIR + "wug2.txt"));
    }

    /**
     * Tests that log prints out commit messages in the right order. Involves
     * init, add, commit, and log.
     */
    @Test
    public void testBasicLog() {
        gitlet("init");
        String commitMessage1 = "initial commit";

        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("add", wugFileName);
        String commitMessage2 = "added wug";
        gitlet("commit", commitMessage2);

        String logContent = null;
        logContent = gitlet("log");
        assertArrayEquals(new String[] { commitMessage2, commitMessage1 },
                extractCommitMessages(logContent));
    }

    /**
     * Convenience method for calling Gitlet's main. Anything that is printed
     * out during this call to main will NOT actually be printed out, but will
     * instead be returned as a string from this method.
     * 
     * Prepares a 'yes' answer on System.in so as to automatically pass through
     * dangerous commands.
     * 
     * The '...' syntax allows you to pass in an arbitrary number of String
     * arguments, which are packaged into a String[].
     */
    private static String gitlet(String... args) {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;
        ByteArrayOutputStream printingResults = new ByteArrayOutputStream();
        try {
            /*
             * Below we change System.out, so that when you call
             * System.out.println(), it won't print to the screen, but will
             * instead be added to the printingResults object.
             */
            System.setOut(new PrintStream(printingResults));

            /*
             * Prepares the answer "yes" on System.In, to pretend as if a user
             * will type "yes". You won't be able to take user input during this
             * time.
             */
            String answer = "yes";
            InputStream is = new ByteArrayInputStream(answer.getBytes());
            System.setIn(is);

            Gitlet.main(args);

        } finally {
            /*
             * Restores System.out and System.in (So you can print normally and
             * take user input normally again).
             */
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        return printingResults.toString();
    }

    /**
     * Returns the text from a standard text file (won't work with special
     * characters).
     */
    private static String getText(String fileName) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Creates a new file with the given fileName and File change
     * =checkTree.getFile(checkTree.getCommit().toString()); String changePath
     * =change.getPath();gives it the text fileText.
     */
    private static void createFile(String fileName, String fileText) {
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
     */
    private static void writeFile(String fileName, String fileText) {
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
     * Deletes the file and all files inside it, if it is a directory.
     */
    private static void recursiveDelete(File d) {
        if (d.isDirectory()) {
            for (File f : d.listFiles()) {
                recursiveDelete(f);
            }
        }
        d.delete();
    }

    /**
     * Returns an array of commit messages associated with what log has printed
     * out.
     */
    private static String[] extractCommitMessages(String logOutput) {
        String[] logChunks = logOutput.split("====");
        int numMessages = logChunks.length - 1;
        String[] messages = new String[numMessages];
        for (int i = 0; i < numMessages; i++) {
            System.out.println(logChunks[i + 1]);
            String[] logLines = logChunks[i + 1].split(LINE_SEPARATOR);
            messages[i] = logLines[3];
        }
        return messages;
    }
    private static String extractError(String input){
        String [] newMsg = input.split("[\\r\\n]+");
        return newMsg[0];
    }
}
