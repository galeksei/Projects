import java.io.Serializable;
import java.util.HashSet;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class Status implements Serializable {
    /**
     * 
     */

    private HashSet<String> branches;
    private HashSet<String> stagedFiles;
    private HashSet<String> markedForRemoval;
    private String currBranch;

    /**
     * constructor
     */
    public Status() {
        currBranch = "*master";
        branches = new HashSet<String>();
        branches.add(currBranch);
        stagedFiles = new HashSet<String>();
        markedForRemoval = new HashSet<String>();
    }

    /**
     * Adds the staged file for the commit
     * 
     * @param path
     */
    public void addToStatus(String path) {
        stagedFiles.add(path);
    }

    /**
     * Checks if there is anything to commit
     * 
     * @return
     */
    public boolean commitEmpty() {
        return (stagedFiles.size() == 0 && markedForRemoval.size() == 0);
    }

    /**
     * Returns the set of files that we need to copy to directory
     * 
     * @return
     */
    public HashSet<String> getFiles() {
        return stagedFiles;
    }

    /**
     * Resets the sets with the new commit
     */
    public void resetStatus() {
        stagedFiles = new HashSet<String>();
        markedForRemoval = new HashSet<String>();
    }

    /**
     * Gets the locations of the files to remove
     * 
     * @return
     */

    public HashSet<String> getRemoved() {
        return markedForRemoval;
    }

    /**
     * Displays the current status of the gitlet
     */
    public void displayStatus() {
        System.out.println("=== Branches ===");
        for (String x : branches) {
            System.out.println(x);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String x : stagedFiles) {
            System.out.println(x);
        }
        System.out.println();
        System.out.println("=== Files Marked For Removal ===");
        for (String x : markedForRemoval) {
            System.out.println(x);
        }
    }

    /**
     * adds a new branch to the list
     * 
     * @param newBranch
     */
    public void addBranch(String newBranch) {
        branches.add(newBranch);
    }

    /**
     * Switches between branches
     */
    public void replaceMainBranch(String newBranch) {
        String temp = currBranch.substring(1);
        branches.remove(currBranch);
        branches.add(temp);
        currBranch = "*" + newBranch;
        branches.remove(newBranch);
        branches.add(currBranch);
    }

    /**
     * removes the branch from the list
     * 
     * @param string
     */
    public void removeBranch(String branch) {
        branches.remove(branch);

    }

    public boolean isStaged(String file) {
        return stagedFiles.contains(file);
    }

    /**
     * adds the file for removal on the next commit
     * 
     * @param file
     */
    public void markForRemoval(String file) {
        if (stagedFiles.contains(file)) {
            stagedFiles.remove(file);
        } else {
            markedForRemoval.add(file);
        }
    }
}
