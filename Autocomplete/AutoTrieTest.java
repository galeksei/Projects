import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

public class AutoTrieTest {
    @Test
    public void testWords(){
        AutoTrie temp = new AutoTrie();
        temp.insert("the", 56271872.00);
        temp.insert("of", 33950064.00);
        temp.insert("and", 29944184.00);
        temp.insert("to", 25956096.00);
        temp.insert("in", 17420636.00);
        temp.insert("i", 11764797.00);
        temp.insert("that", 11073318.00);
        temp.topWords("", 8);
    }

    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(AutoTrieTest.class);

    }

}
