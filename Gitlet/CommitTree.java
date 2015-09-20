import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class CommitTree implements Serializable {
    private HashMap<String, HashSet<Commit>> messageToCommits;
    private HashMap<String, TreeSet<Commit>> mainTree;
    private Integer commit;
    private String currBranch;
    private HashMap<String, String> fileToLoc;
    private Integer size;
    private HashMap<String, Commit> branchToHead;
    private HashMap<String, Commit> idToCommit;

    public CommitTree() {
        idToCommit = new HashMap<String, Commit>();
        branchToHead = new HashMap<String, Commit>();
        size = 0;
        fileToLoc = new HashMap<String, String>();
        commit = 0;
        currBranch = "master";
        mainTree = new HashMap<String, TreeSet<Commit>>();
        Commit init = new Commit();
        TreeSet<Commit> commitSet = new TreeSet<Commit>();
        commitSet.add(init);
        mainTree.put(currBranch, commitSet);
        branchToHead.put(currBranch, init);
        HashSet<Commit> temp = new HashSet<Commit>();
        messageToCommits = new HashMap<String, HashSet<Commit>>();
        messageToCommits.put("initial commit", temp);
        messageToCommits.get("initial commit").add(init);
        mainTree.get(currBranch).add(init);
        idToCommit.put("0", init);
    }

    /**
     * Stores the files and their locations in a commit tree
     * 
     * @param file
     * @param loc
     */
    public void addLocation(String file, String loc) {
        fileToLoc.put(file, loc);
    }

    /**
     * adds a branch to the map of branches to commits
     * 
     * @param name
     */
    public void addBranch(String name) {
        TreeSet<Commit> temp = new TreeSet<Commit>();
        mainTree.put(name, temp);
        mainTree.get(name).add(getHeadCommit());
        branchToHead.put(name, getHeadCommit());
    }

    /**
     * removes the branch with the given name
     * 
     * @param name
     */
    public void removeBranch(String name) {
        mainTree.remove(name);
    }

    /**
     * adds the commit to the data Structures
     * 
     * @param commitToAdd
     */
    public void addData(Commit commitToAdd) {
        addToBranch(commitToAdd);
        if (!messageToCommits.containsKey(commitToAdd.getMessage())) {
            HashSet<Commit> temp = new HashSet<Commit>();
            messageToCommits.put(commitToAdd.getMessage(), temp);
        }
        messageToCommits.get(commitToAdd.getMessage()).add(commitToAdd);
        branchToHead.put(currBranch, commitToAdd);
        idToCommit.put(commit.toString(), commitToAdd);
        fileToLoc = new HashMap<String, String>(
                commitToAdd.getUpdatedFileToLoc());
    }

    /**
     * Add to the current branch
     * 
     * @param commitToAdd
     */
    public void addToBranch(Commit commitToAdd) {
        mainTree.get(currBranch).add(commitToAdd);
    }

    /**
     * Get a string of a current commit
     * 
     * @return
     * 
     */
    public String getCommit() {
        return commit.toString();
    }

    /**
     * Returns whether there is an id
     * 
     * @param id
     * @return
     */
    public boolean hasId(String id) {
        if (idToCommit.containsKey(id)) {
            return true;
        }
        return false;
    }

    /**
     * Get an Integer of a current commit
     * 
     * @return
     */
    public Integer getCommitInt() {
        return commit;
    }

    /**
     * modify the current commit
     * 
     * @param num
     */
    public void setCommit(Integer num) {
        commit = num;
    }

    /**
     * returns the commits in that branch
     * 
     * @return
     */
    public TreeSet<Commit> getCommits() {
        return mainTree.get(currBranch);
    }

    /**
     * Gets a set of commits from a specific branch
     * 
     * @param key
     * @return
     */
    public TreeSet<Commit> getCommits(String key) {
        return mainTree.get(key);
    }

    /**
     * returns the mapping of locations to files
     * 
     * @return
     */
    public HashMap<String, String> getMap() {
        return fileToLoc;
    }

    public void setMap(HashMap<String, String> newMap) {
        fileToLoc = newMap;
    }

    /**
     * Returns all the branches of the main commit tree
     * 
     * @return
     */

    public Set<String> getBranches() {
        return mainTree.keySet();
    }

    /**
     * Increment the size for commit id
     */
    public void addSize() {
        size += 1;
    }

    /**
     * get the number of IDs
     * 
     * @return
     */
    public Integer getSize() {
        return size;
    }

    /**
     * returns a map with message to commit pairs
     * 
     * @return
     */

    public HashMap<String, HashSet<Commit>> getMessageToCommits() {
        return messageToCommits;
    }

    /**
     * returns the head of a given branch
     * 
     * @param branch
     * @return
     */
    public Commit getHeadCommit(String branch) {
        return branchToHead.get(branch);
    }

    /**
     * Returns the head of the current branch
     * 
     * @return
     */
    public Commit getHeadCommit() {
        return branchToHead.get(currBranch);
    }

    /**
     * Checks whether there is a branch with a specified name
     * 
     * @param br
     * @return
     */
    public boolean hasBranch(String br) {
        return mainTree.containsKey(br);
    }

    /**
     * Gets the name of the current branch
     * 
     * @return
     */
    public String getCurrentBranch() {
        return currBranch;
    }

    /**
     * Sets a new current branch
     * 
     * @param branch
     */
    public void setCurrentBranch(String branch) {
        currBranch = branch;
    }

    /**
     * Get commit from a given id
     * 
     * @param id
     * @return
     */
    public Commit getCommitFromId(String id) {
        return idToCommit.get(id);
    }

    /**
     * get all of the Ids
     * 
     * @return
     */
    public Set<String> getIdSet() {
        return idToCommit.keySet();
    }

    /**
     * Checks if the file is present
     * 
     * @param file
     * @return
     */
    public boolean containsFile(String file) {
        return fileToLoc.containsKey(file);
    }

    /**
     * Checks if there is an id with that name
     * 
     * @param id
     * @return
     */
    public boolean containsId(String id) {
        return idToCommit.containsKey(id);
    }

    /**
     * Sets a new head commit
     * 
     * @param commit2
     */
    public void setHeadCommit(Commit commit2) {
        branchToHead.put(currBranch, commit2);

    }

    /**
     * Removes all the commits if they are ahead otherwise adds to the right
     * place
     */
    public void alterCommitSet(Commit upTo) {

        mainTree.put(currBranch, upTo.getPrevCommits());
        mainTree.get(currBranch).add(upTo);
        // TODO Auto-generated method stub

    }

    /**
     * checks if the branch contains the given commit
     * 
     * @param headCommit
     * @return
     */
    public boolean branchContainsCommit(Commit commitToCheck) {
        return mainTree.get(currBranch).contains(commitToCheck);
    }

    /**
     * adds a commit to a branch
     * 
     * @param commitToAdd
     */
    public void addCommit(Commit commitToAdd) {
        mainTree.get(currBranch).add(commitToAdd);
    }

    /**
     * removes commit from a branch
     * 
     * @param commitToRemove
     */
    public void removeCommit(Commit commitToRemove) {
        mainTree.get(currBranch).remove(commitToRemove);
    }
}
