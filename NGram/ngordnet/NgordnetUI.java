package ngordnet;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;

/**
 * Provides a simple user interface for exploring WordNet and NGram data.
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu credit to Josh Hug
 *         for starter code
 */
public class NgordnetUI {
    /**
     * provides a user interface to incorporate all the words and plot trends
     * @param args
     */
    public static void main(String[] args) {
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");

        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();
        NGramMap ngm = new NGramMap(wordFile, countFile);
        WordNet wn = new WordNet(synsetFile, hyponymFile);
        System.out
                .println("\nBased on ngordnetui.config, using the following: "
                        + wordFile + ", " + countFile + ", " + synsetFile
                        + ", and " + hyponymFile + ".");
        int startDate = 1500;
        int endDate = 2010;
        String word = null;
        int year = 0;
        while (true) {

            System.out.print("> ");
            String line = StdIn.readLine();
            String[] rawTokens = line.split(" ");
            String command = rawTokens[0];
            String[] tokens = new String[rawTokens.length - 1];
            System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
            try {
                switch (command) {
                case "quit":
                    return;
                case "help":
                    In help = new In("./ngordnet/help.txt");
                    String helpStr = help.readAll();
                    System.out.println(helpStr);
                    break;
                case "range":

                    startDate = Integer.parseInt(tokens[0]);
                    endDate = Integer.parseInt(tokens[1]);
                    System.out.println("Start date: " + startDate);
                    System.out.println("End date: " + endDate);

                    break;
                case "count":

                    word = tokens[0];
                    year = Integer.parseInt(tokens[1]);
                    if (wn.isNoun(word)) {
                        System.out.println(ngm.countInYear(word, year));
                    } else {
                        System.out
                                .println("The word or the year is not in the database");
                    }
                    break;
                case "hyponyms":
                    word = tokens[0];
                    if (wn.isNoun(word)) {
                        System.out.println("Hypnoyms of " + word + ":");
                        System.out.println(wn.hyponyms(word));
                    }

                    break;
                case "history":

                    Plotter.plotAllWords(ngm, tokens, startDate, endDate);

                    break;
                case "hypohist":
                    Plotter.plotCategoryWeights(ngm, wn, tokens, startDate,
                            endDate);
                    break;
                case "wordlength":
                    Plotter.plotProcessedHistory(ngm, startDate, endDate,
                            new WordLengthProccessor());
                    break;
                case "zipf":
                    year = Integer.parseInt(tokens[0]);
                    Plotter.plotZipfsLaw(ngm, year);
                    break;

                default:
                    System.out.println("Invalid command.");
                    break;
                }
            } catch (RuntimeException e) {
                System.out.println("Invalid input please type in help for correct examples");
            }
        }
    }
}
