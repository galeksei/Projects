import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class Commit implements Comparable<Commit>, Serializable {
    /**
     * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
     */

    private String date;
    private String id;
    private String message;
    private HashSet<String> locations;
    private HashMap<String, String> fileToLoc;
    private TreeSet<Commit> prevCommits;

    /**
     * constructor with no parameters for initial commit
     */
    public Commit() {
        fileToLoc = new HashMap<String, String>();
        id = "0";
        locations = new HashSet<String>();
        message = "initial commit";
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
        prevCommits = new TreeSet<Commit>();

    }

    /**
     * Constructor for a new commit object
     * 
     * @param fileMap
     * @param paths
     * @param message
     * @param commitName
     * @param commitHist
     */
    public Commit(HashMap<String, String> fileMap, HashSet<String> paths,
            String message, String commitName, TreeSet<Commit> commitHist) {
        id = commitName;
        locations = paths;
        this.message = message;
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
        fileToLoc = new HashMap<String, String>(fileMap);
        prevCommits = commitHist;
    }

    /**
     * Generates commit string for the log file
     * 
     * @param commit
     * @param commitMessage
     * @return
     *
     */
    public String generateMessage() {
        String input = "";
        input += "\r\n" + "====" + "\r\n" + "Commit " + id + "." + "\r\n"
                + date + "\r\n" + message;
        return input;
    }

    /**
     * Copies over the commit, but with a different id
     * 
     * @param origin
     * @param name
     * @return
     */
    public Commit commitCopy(Commit origin, String name) {
        Commit temp = new Commit(origin.fileToLoc, origin.locations,
                origin.message, name, origin.prevCommits);
        return temp;
    }

    /**
     * Copies over the commit, different id and alters the message as well
     * 
     * @param origin
     * @param name
     * @param newMessage
     * @return
     */
    public Commit commitCopy(Commit origin, String name, String newMessage) {
        Commit temp = new Commit(origin.fileToLoc, origin.locations,
                newMessage, name, origin.prevCommits);
        return temp;
    }

    /**
     * get the commit id
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * checks if it is the same message
     */
    public boolean checkMessage(String compare) {
        if (message.equals(compare)) {
            return true;
        }
        return false;
    }

    /**
     * adds a mapping from the file to the exact location of the commit
     * 
     * @param file
     * @param loc
     */
    public void addLocation(String file, String loc) {
        fileToLoc.put(file, loc);
    }

    /**
     * Returns a set with all the files and their locations
     * 
     * @return
     */
    public HashMap<String, String> getFileLoc() {
        return fileToLoc;
    }

    /**
     * returns a message from the commit
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    @Override
    public int compareTo(Commit o) {

        return id.compareToIgnoreCase(o.getId()) * -1;
    }

    /**
     * returns a set of all of the location
     * 
     * @return
     */
    public String getLocation(String file) {
        return fileToLoc.get(file);
    }

    /**
     * Checks whether the commit contains a given filename
     * 
     * @param file
     * @return
     */
    public boolean containsFile(String file) {
        return fileToLoc.containsKey(file);
    }

    /**
     * removes an entry with a specified file name
     * 
     * @param file
     */
    public void removeEntry(String file) {
        String location = fileToLoc.get(file);
        fileToLoc.remove(file);
        locations.remove(location);

    }

    /**
     * returns a set of all the previous commits
     * 
     * @return
     */
    public TreeSet<Commit> getPrevCommits() {
        return prevCommits;
    }

    /**
     * gets fileToLoc map to add to a commit tree
     * 
     * @return
     */
    public HashMap<String, String> getUpdatedFileToLoc() {
        return fileToLoc;
    }

    public boolean same(Commit other) {
        return id.equals(other.getId());

    }

    public void setFileLoc(HashMap<String, String> bCommitFiles) {
       fileToLoc = bCommitFiles;
        
    }
}
