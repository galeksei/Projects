import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
class Gitlet implements Serializable {
    CommitTree tree;
    Status status;

    public Gitlet() {
        tree = new CommitTree();
        status = new Status();
    }

    public static void main(String[] args) {
        Gitlet currCommit = loadGitlet();

        String command = args[0];
        switch (command) {
            case "init":
                init();
                break;
            case "add":
                add(currCommit, args);
                break;
            case "rm":
                rm(currCommit, args);
                break;
            case "commit":
                commit(currCommit, args);
                break;
            case "log":
                printLog(currCommit.tree.getCommits());
                break;
            case "global-log":
                for (String x : currCommit.tree.getIdSet()) {
                    System.out.println(currCommit.tree.getCommitFromId(x)
                            .generateMessage());
                }
                break;
            case "find":
                find(currCommit, args);
                break;
            case "status":
                currCommit.status.displayStatus();
                break;
            case "checkout":
                checkout(currCommit, args);
                break;
            case "branch":
                branch(currCommit, args);
                break;
            case "rm-branch":
                removeBranch(currCommit, args);
                break;
            case "reset":
                reset(currCommit, args);
                break;
            case "merge":
                merge(currCommit, args);
                break;
            case "rebase":
                rebase(currCommit, args);
                break;
            case "i-rebase":
                iRebase(currCommit, args);
                break;
            default:
                System.out.println("Please enter a valid command");
        }

    }

    /**
     * Performs the interactive rebase function
     * 
     * @param currCommit
     * @param args
     */
    private static void iRebase(Gitlet currCommit, String[] args) {
        if (!isSure()) {
            return;
        } else if (rebaseCheck(currCommit, args)) {
            return;
        } else if (sameHist(currCommit, args[1])) {
            Commit toChange = currCommit.tree.getHeadCommit(args[1]);
            currCommit.tree.setHeadCommit(toChange);
            currCommit.tree.setCommit(Integer.parseInt(toChange.getId()));
        } else {
            Commit split = findSplitBranch(currCommit, args);
            HashMap<String, String> splitMap = split.getFileLoc();
            TreeSet<Commit> tempCurr = currCommit.tree.getCommits();
            TreeSet<Commit> tempBranch = currCommit.tree.getCommits(args[1]);
            TreeSet<Commit> currCommits = (TreeSet<Commit>) tempCurr.subSet(
                    currCommit.tree.getHeadCommit(), true, split, true);
            TreeSet<Commit> branchCommits = (TreeSet<Commit>) tempBranch
                    .subSet(currCommit.tree.getHeadCommit(args[1]), true,
                            split, true);
            int count = 1;
            int size = currCommits.size();
            for (Commit x : currCommits) {
                System.out.println("Currently replaying:");
                System.out.println(x.generateMessage());
                String question = "Would you like to (c)ontinue, (s)kip this commit, ";
                question += "or change this commit's (m)essage?";
                System.out.println(question);
                Scanner in = new Scanner(System.in);
                in = new Scanner(System.in);
                String answer = in.nextLine();
                if (answer.equals("s")) {
                    while (true) {
                        if (count == 1 || count == size) {
                            String error = "You can not skip, please enter (c)ontinue";
                            error += " or (m)essage";
                            System.out.println(error);
                            answer = in.nextLine();
                        }
                        if (!answer.equals("s")) {
                            break;
                        }
                    }
                } else if (answer.equals("s") && count != 1 && count != size) {
                    continue;
                } else if (answer.equals("c") || answer.equals("m")) {
                    currCommit.tree.removeCommit(x);
                    currCommit.tree.addSize();
                    String newId = currCommit.tree.getSize().toString();
                    Commit commitToAdd = x.commitCopy(x, newId);

                    HashMap<String, String> newMap = propogate(
                            x.getFileLoc(),
                            currCommit.tree.getHeadCommit(args[1]).getFileLoc(),
                            splitMap);
                    commitToAdd.setFileLoc(newMap);

                    currCommit.tree.addData(commitToAdd);
                    if (currCommit.tree.getHeadCommit().same(x)) {
                        currCommit.tree.setHeadCommit(commitToAdd);

                    }
                    currCommit.tree.addCommit(commitToAdd);
                }
            }
            for (Commit x : branchCommits) {
                currCommit.tree.addCommit(x);
            }
            updateFiles(currCommit);
        }
        addSerializeFile(currCommit);
    }

    /**
     * Checks the failure cases for rebase
     * 
     * @param currCommit
     * @param args
     * @return
     */
    private static boolean rebaseCheck(Gitlet currCommit, String[] args) {
        boolean temp = true;
        if (args.length != 2) {
            System.out.println("Please input the branch name");
            temp = false;
        } else if (!currCommit.tree.hasBranch(args[1])) {
            System.out.println("A branch with that name does not exist.");
            temp = false;
        } else if (args[1].equals(currCommit.tree.getCurrentBranch())) {
            System.out.println("Cannot rebase a branch onto itself.");
            temp = false;
        } else if (currCommit.tree.branchContainsCommit(currCommit.tree
                .getHeadCommit(args[1]))) {
            System.out.println("Already up-to-date.");
            temp = false;
        }

        return temp;
    }

    /**
     * performs the rebase functionality
     * 
     * @param currCommit
     * @param args
     */
    private static void rebase(Gitlet currCommit, String[] args) {
        if (!isSure()) {
            return;
        } else if (!rebaseCheck(currCommit, args)) {
            return;
        } else if (sameHist(currCommit, args[1])) {
            Commit toChange = currCommit.tree.getHeadCommit(args[1]);
            currCommit.tree.setHeadCommit(toChange);
            currCommit.tree.setCommit(Integer.parseInt(toChange.getId()));
        } else {
            Commit split = findSplitBranch(currCommit, args);
            HashMap<String, String> splitMap = split.getFileLoc();
            TreeSet<Commit> tempCurr = currCommit.tree.getCommits();
            TreeSet<Commit> tempBranch = currCommit.tree.getCommits(args[1]);
            TreeSet<Commit> currCommits = (TreeSet<Commit>) tempCurr.subSet(
                    currCommit.tree.getHeadCommit(), true, split, true);
            TreeSet<Commit> branchCommits = (TreeSet<Commit>) tempBranch
                    .subSet(currCommit.tree.getHeadCommit(args[1]), true,
                            split, true);
            for (Commit x : currCommits) {
                currCommit.tree.removeCommit(x);
                currCommit.tree.addSize();
                String newId = currCommit.tree.getSize().toString();
                Commit commitToAdd = x.commitCopy(x, newId);

                HashMap<String, String> newMap = propogate(x.getFileLoc(),
                        currCommit.tree.getHeadCommit(args[1]).getFileLoc(),
                        splitMap);
                commitToAdd.setFileLoc(newMap);

                currCommit.tree.addData(commitToAdd);
                if (currCommit.tree.getHeadCommit().same(x)) {
                    currCommit.tree.setHeadCommit(commitToAdd);
                }
            }
            for (Commit x : branchCommits) {
                currCommit.tree.addCommit(x);
            }
            updateFiles(currCommit);
        }
        addSerializeFile(currCommit);
    }

    private static HashMap<String, String> propogate(
            HashMap<String, String> fileLocB,
            HashMap<String, String> fileLocCurr,
            HashMap<String, String> splitMap) {
        for (String fileToCompare : fileLocB.keySet()) {
            if (splitMap.containsKey(fileToCompare)
                    && fileLocCurr.containsKey(fileToCompare)) {
                if (splitMap.get(fileToCompare).equals(
                        fileLocCurr.get(fileToCompare))) {
                    fileLocCurr.put(fileToCompare, fileLocB.get(fileToCompare));
                }
            }
        }
        return fileLocCurr;
    }

    /**
     * updates files in the current directory
     * 
     * @param splitMap
     * @param currCommit
     */
    private static void updateFiles(Gitlet currCommit) {
        HashMap<String, String> currHeadFiles = currCommit.tree.getHeadCommit()
                .getFileLoc();
        currCommit.tree.setMap(currHeadFiles);
        for (String fileName : currHeadFiles.keySet()) {
            FileManip temp = new FileManip(currHeadFiles.get(fileName));
            temp.copyFile(fileName);
        }

    }

    /**
     * gets a map of all the modified files compared to split commit
     * 
     * @param split
     * @param commitsCheck
     * @return
     */
    public static HashMap<String, String> changedFilesLoc(Commit split,
            TreeSet<Commit> commitsCheck) {
        HashMap<String, String> temp = new HashMap<String, String>();
        HashMap<String, String> splitMap = split.getFileLoc();
        for (Commit x : commitsCheck) {
            HashMap<String, String> commitMap = x.getFileLoc();
            for (String y : commitMap.keySet()) {
                if (!splitMap.containsKey(y)
                        || !splitMap.get(y).equals(commitMap.get(y))) {
                    temp.put(y, commitMap.get(y));
                }
            }
        }
        return temp;
    }

    /**
     * asks the user whether they are sure they want to continue with the action
     * 
     * @return
     */
    public static boolean isSure() {
        Scanner in = new Scanner(System.in);
        in = new Scanner(System.in);
        String danger = "This action might result in a loss of data,";
        danger += " do you want to continue? (yes/no)";
        System.out.println(danger);
        String answer = in.nextLine();
        if (answer.equals("no")) {
            return false;
        }
        return true;
    }

    /**
     * checks whether the given branch is in the history of the current branch
     * 
     * @param currCommit
     * @param string
     * @return
     */
    private static boolean sameHist(Gitlet currCommit, String givenBranch) {
        TreeSet<Commit> currCommits = currCommit.tree.getCommits();
        TreeSet<Commit> branchCommits = currCommit.tree.getCommits(givenBranch);
        for (Commit x : currCommits) {
            if (!branchCommits.contains(x)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs a merge operation
     * 
     * @param currCommit
     * @param args
     */
    private static void merge(Gitlet currCommit, String[] args) {
        if (!isSure()) {
            return;
        } else if (args.length != 2) {
            System.out.println("Please input the branch name");
        } else if (!currCommit.tree.hasBranch(args[1])) {
            System.out.println("A branch with that name does not exist.");
        } else if (args[1].equals(currCommit.tree.getCurrentBranch())) {
            System.out.println("Cannot merge a branch with itself.");
        } else {
            HashMap<String, String> splitMap = findSplitBranch(currCommit, args)
                    .getFileLoc();
            HashMap<String, String> headMap = currCommit.tree.getHeadCommit()
                    .getFileLoc();
            HashMap<String, String> mergeMap = currCommit.tree.getHeadCommit(
                    args[1]).getFileLoc();
            for (String mergeFile : mergeMap.keySet()) {
                if (splitMap.containsKey(mergeFile)
                        && headMap.containsKey(mergeFile)) {
                    if (splitMap.get(mergeFile).equals(headMap.get(mergeFile))
                            && !headMap.get(mergeFile).equals(
                                    mergeMap.get(mergeFile))) {
                        FileManip origin = new FileManip(
                                mergeMap.get(mergeFile));
                        origin.copyFile(mergeFile);
                    } else if (!splitMap.get(mergeFile).equals(
                            headMap.get(mergeFile))
                            && !splitMap.get(mergeFile).equals(
                                    mergeMap.get(mergeFile))) {
                        FileManip origin = new FileManip(
                                mergeMap.get(mergeFile));
                        origin.copyFile(mergeFile + ".conflicted");
                    }
                } else if (!splitMap.containsKey(mergeFile)
                        && headMap.containsKey(mergeFile)) {
                    FileManip origin = new FileManip(mergeMap.get(mergeFile));
                    if (!origin.isSame(headMap.get(mergeFile))) {
                        origin.copyFile(mergeFile + ".conflicted");
                    }
                }
            }
        }
    }

    /**
     * find the split commit between current branch and given branch
     * 
     * @param currCommit
     * @return
     */
    private static Commit findSplitBranch(Gitlet currCommit, String[] args) {
        TreeSet<Commit> currCommits = currCommit.tree.getCommits();
        TreeSet<Commit> mergeCommits = currCommit.tree.getCommits(args[1]);
        Commit splitCommit = null;
        for (Commit x : currCommits) {
            if (mergeCommits.contains(x)) {
                splitCommit = x;
                break;
            }
        }
        return splitCommit;
    }

    /**
     * removes the given file from the commit or unstages it
     * 
     * @param currCommit
     * @param args
     */
    private static void rm(Gitlet currCommit, String[] args) {
        if (args.length != 2) {
            System.out.println("Please input the file to remove");
            return;
        }
        Commit headCommit = currCommit.tree.getHeadCommit();
        if (!headCommit.containsFile(args[1])
                && !currCommit.status.isStaged(args[1])) {
            System.out.println("No reason to remove the file.");
        } else {
            currCommit.status.markForRemoval(args[1]);
        }
        addSerializeFile(currCommit);
    }

    /**
     * Performs a reset function
     * 
     * @param currCommit
     * @param args
     */
    private static void reset(Gitlet currCommit, String[] args) {
        if (!isSure()) {
            return;
        } else if (args.length != 2) {
            System.out.println("Please input a commit ID");

        } else if (!currCommit.tree.containsId(args[1])) {
            System.out.println("No commit with that id exists.");

        } else {
            Commit commit = currCommit.tree.getCommitFromId(args[1]);
            HashMap<String, String> fileToLoc = commit.getUpdatedFileToLoc();
            for (String x : fileToLoc.keySet()) {
                FileManip origin = new FileManip(fileToLoc.get(x));
                origin.copyFile(x);
            }
            currCommit.tree.setCommit(Integer.parseInt(args[1]));
            currCommit.tree.setHeadCommit(commit);
            currCommit.tree.alterCommitSet(commit);
            addSerializeFile(currCommit);
        }
    }

    /**
     * performs a remove Branch function
     * 
     * @param currCommit
     * @param args
     */
    private static void removeBranch(Gitlet currCommit, String[] args) {
        if (args.length != 2) {
            System.out.println("Please input a branch name");

        } else if (currCommit.tree.getCurrentBranch().equals(args[1])) {
            System.out.println("Cannot remove the current branch.");

        } else if (!currCommit.tree.getBranches().contains(args[1])) {
            System.out.println("A branch with that name does not exist.");

        } else {
            currCommit.tree.removeBranch(args[1]);
            currCommit.status.removeBranch(args[1]);
            addSerializeFile(currCommit);
        }
    }

    /**
     * adds a branch
     * 
     * @param currCommit
     * @param args
     */
    private static void branch(Gitlet currCommit, String[] args) {
        if (args.length != 2) {
            System.out.println("Please input a branch name");
        } else if (currCommit.tree.getBranches().contains(args[1])) {
            System.out.println("A branch with that name already exists.");
        } else {
            currCommit.status.addBranch(args[1]);
            currCommit.tree.addBranch(args[1]);
            addSerializeFile(currCommit);
        }
    }

    /**
     * performs the find function
     * 
     * @param currCommit
     * @param args
     */
    private static void find(Gitlet currCommit, String[] args) {
        if (args.length != 2) {
            System.out.println("Specify which message you are looking for.");
            return;
        }
        HashMap<String, HashSet<Commit>> messageToCommit = currCommit.tree
                .getMessageToCommits();
        if (!messageToCommit.containsKey(args[1])) {
            System.out.println("Found no commit with that message.");

        } else {
            for (Commit x : messageToCommit.get(args[1])) {
                System.out.println(x.getId());
            }
        }
    }

    /**
     * performs the checkout function
     * 
     * @param currCommit
     * @param args
     */
    private static void checkout(Gitlet currCommit, String[] args) {
        if (args.length < 2 && args.length > 3) {
            System.out
                    .println("Please input one of the following combinations: "
                            + "\n File Name \n Commit Id and File Name \n Branch Name");
            return;
        }
        if (!isSure()) {
            return;
        }
        if (args.length == 2) {
            if (currCommit.tree.getBranches().contains(args[1])) {
                if (currCommit.tree.getCurrentBranch().equals(args[1])) {
                    System.out
                            .println("No need to checkout the current branch.");
                    return;
                }
                Commit headCommit = currCommit.tree.getHeadCommit(args[1]);
                HashMap<String, String> locations = headCommit.getFileLoc();
                HashMap<String, String> currentFiles = currCommit.tree
                        .getHeadCommit().getFileLoc();
                for (String x : currentFiles.keySet()) {
                    if (locations.containsKey(x)) {
                        FileManip temp = new FileManip(locations.get(x));
                        temp.copyFile(x);
                    }
                }
                currCommit.tree.setCurrentBranch(args[1]);
                currCommit.status.replaceMainBranch(args[1]);
                addSerializeFile(currCommit);
                return;
            }
            Commit headCommit = currCommit.tree.getHeadCommit();
            if (currCommit.tree.containsFile(args[1])) {
                FileManip dest = new FileManip(headCommit.getLocation(args[1]));
                dest.copyFile(args[1]);
                return;
            }
            String error = "File does not exist in the most recent commit,";
            error += " or no such branch exists.";
            System.out.println(error);
            return;
        } else {
            if (!currCommit.tree.hasId(args[1])) {
                System.out.println("No commit with that id exists.");
                return;

            }
            Commit commit = currCommit.tree.getCommitFromId(args[1]);

            if (!commit.containsFile(args[2])) {
                System.out.println("No such file exists in this commit.");

            } else {
                System.out.println(commit.getLocation(args[2]));
                FileManip dest = new FileManip(commit.getLocation(args[2]));
                dest.copyFile(args[2]);
            }
        }
    }

    /**
     * performs commit action
     * 
     * @param currCommit
     * @param args
     */
    private static void commit(Gitlet currCommit, String[] args) {
        if (args.length < 2 || (args[1].trim().length() == 0)) {
            System.out.println("Please enter a commit message");
            return;
        }
        if (currCommit.status.commitEmpty()) {
            System.out.println("There are no changes staged for commit");
            return;
        } else {
            currCommit.tree.addSize();
            Integer commitId = currCommit.tree.getSize();
            currCommit.tree.setCommit(commitId);
            FileManip commitFolder = new FileManip(".gitlet/"
                    + commitId.toString() + "/");
            commitFolder.makeDir();
            String message = args[1];
            HashSet<String> filesToRemove = currCommit.status.getRemoved();
            HashSet<String> filePaths = currCommit.status.getFiles();
            HashSet<String> commitFilePaths = new HashSet<String>();
            for (String x : filePaths) {
                FileManip origin = new FileManip(x);
                String copyLoc = commitFolder.getPath() + x;
                currCommit.tree.addLocation(x, copyLoc);
                commitFilePaths.add(copyLoc);
                origin.copyFile(copyLoc);

            }
            TreeSet<Commit> commitsToAdd = new TreeSet<Commit>();
            for (Commit x : currCommit.tree.getCommits()) {
                commitsToAdd.add(x);
            }
            Commit toCommit = new Commit(currCommit.tree.getMap(),
                    commitFilePaths, message, commitId.toString(), commitsToAdd);
            for (String x : filesToRemove) {
                toCommit.removeEntry(x);
            }
            currCommit.tree.addData(toCommit);
            currCommit.status.resetStatus();
            addSerializeFile(currCommit);
        }
    }

    /**
     * performs the add function
     * 
     * @param currCommit
     * @param args
     */
    public static void add(Gitlet currCommit, String[] args) {
        if (args.length != 2) {
            System.out.println("Specify which file to add.");
            return;
        }
        FileManip fileToAdd = new FileManip(args[1]);
        FileManip prevFile = new FileManip(".gitlet/"
                + currCommit.tree.getCommit() + "/" + args[1]);
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
        } else if (!prevFile.exists()) {
            currCommit.status.addToStatus(args[1]);
            addSerializeFile(currCommit);
        } else if (fileToAdd.isSame(prevFile.getPath())) {
            System.out.println("File has not been modified since the last commit.");
        } else {
            currCommit.status.addToStatus(args[1]);
            addSerializeFile(currCommit);
        }
    }

    /**
     * calls the serialize method
     * 
     * @param currCommit
     */
    public static void addSerializeFile(Gitlet currCommit) {
        try {
            serialize(currCommit, ".gitlet/currGitlet.ser");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void init() {
        FileManip gitlet = new FileManip(".gitlet");
        if (gitlet.exists()) {
            String error = "A gitlet version control system already ";
            error += "exists in the current directory.";
            System.out.println(error);
            return;
        } else {
            gitlet.makeDir();
            Gitlet gitletAtt = new Gitlet();
            addSerializeFile(gitletAtt);
        }
    }

    /**
     * Prints the commits
     * 
     * @param commits
     */
    public static void printLog(TreeSet<Commit> commits) {
        for (Commit x : commits) {
            System.out.println(x.generateMessage());
        }
    }

    /**
     * loads the serializable
     * 
     * @return
     */
    private static Gitlet loadGitlet() {
        Gitlet currGitlet = null;
        File currGitletFile = new File(".gitlet/currGitlet.ser");
        if (currGitletFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(currGitletFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                currGitlet = (Gitlet) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading CommitTree.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading GitletAtt.";
                System.out.println(msg);
            }
        }
        return currGitlet;
    }

    /**
     * serializes the GitletAtt and all of the data structures
     * 
     * @param tree
     * @param fileName
     * @throws IOException
     */
    private static void serialize(Gitlet gitletAtt, String fileName)
            throws IOException {
        try {
            File gitletFile = new File(fileName);
            FileOutputStream fileOut = new FileOutputStream(gitletFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(gitletAtt);
        } catch (IOException e) {
            System.err.println(e);
            String msg = "IOException while saving Gitlet.";
            System.out.println(msg);
        }
    }
}
