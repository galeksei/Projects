package ngordnet;

import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

public class WordSetTester {
	@Test
	public void testNoun() {
		WordNet wn = new WordNet("./wordnet/synsets11.txt",
				"./wordnet/hyponyms11.txt");
		assertEquals(true, wn.isNoun("jump"));
		assertEquals(false, wn.isNoun("potato"));
		assertEquals(true, wn.nouns().contains("jump"));
	}

	@Test
	public void testHyponyms() {
		WordNet wn = new WordNet("./wordnet/synsets11.txt",
				"./wordnet/hyponyms11.txt");
		Set<String> test = wn.hyponyms("jump");
		assertEquals(true, test.contains("leap"));
	}

	@Test
	public void testBigSet() {
		WordNet wn = new WordNet("./wordnet/synsets.txt",
				"./wordnet/hyponyms.txt");
		Set<String> test = wn.hyponyms("animal");
		assertEquals(true, test.contains("cat"));
		assertEquals(false, test.contains("purple"));
	}

	/** Runs tests. */
	public static void main(String[] args) {
		jh61b.junit.textui.runClasses(WordSetTester.class);

	}

}
