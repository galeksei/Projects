package ngordnet;

import java.util.Collection;
/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class WordLengthProccessor implements YearlyRecordProcessor{
    /**
     * Calculates the average length of words
     * @param yearlyRecord 
     * @return the average length of a word in a given year
     */
    @Override
    public double process(YearlyRecord yearlyRecord) {
        if(yearlyRecord == null){
            return 0;
        }
        double totalLength = 0;
        double count = 0;
        Collection<String> words = yearlyRecord.words();
        for(String word : words){
            totalLength+=word.length()*yearlyRecord.count(word);
            count += yearlyRecord.count(word);
        }
        return (totalLength/count);
    }
    
}