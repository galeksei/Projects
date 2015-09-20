package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;
import com.xeiam.xchart.ChartBuilder;

/**
 * Credit to Josh Hug and his example plotter
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */

public class Plotter {
    /**
     * Makes a plot showing overlaid individual normalized count for every word
     * in words from startYear to endYear using NGM as data source
     * 
     * @param ngm
     * @param words
     * @param startYear
     */
    public static void plotAllWords(NGramMap ngm, String[] words,
            int startYear, int endYear) {
        Chart chart = new ChartBuilder().width(800).height(600)
                .xAxisTitle("years").yAxisTitle("data").build();

        for (String word : words) {
            TimeSeries<Double> bundle = ngm.weightHistory(word, startYear,
                    endYear);
            chart.addSeries(word, bundle.years(), bundle.data());
        }
        new SwingWrapper(chart).displayChart();
    }

    /**
     * Creates overlaid category weight plots for each category label in
     * CATEGORYLABELS from STARTYEAR to ENDYEAR using NGM and WN as data
     * sources.
     * 
     * @param ngm
     * @param wn
     * @param categoryLabels
     * @param startYear
     * @param endYear
     */
    public static void plotCategoryWeights(NGramMap ngm, WordNet wn,
            String[] categoryLabels, int startYear, int endYear) {
        Chart chart = new ChartBuilder().width(800).height(600)
                .xAxisTitle("years").yAxisTitle("data").build();
        for (String categoryLabel : categoryLabels) {
            Set<String> words = wn.hyponyms(categoryLabel);

            TimeSeries<Double> bundle = ngm.summedWeightHistory(words,
                    startYear, endYear);
            chart.addSeries(categoryLabel, bundle.years(), bundle.data());
        }
        new SwingWrapper(chart).displayChart();
    }

    /**
     * Creates a plot of the total normalized count of
     * WN.hyponyms(CATEGORYLABEL) from STARTYEAR to ENDYEAR using NGM and WN as
     * data sources.
     * 
     * @param ngm
     * @param wn
     * @param categoryLabel
     * @param startYear
     * @param endYear
     */
    public static void plotCategoryWeights(NGramMap ngm, WordNet wn,
            String categoryLabel, int startYear, int endYear) {
        Set<String> words = wn.hyponyms(categoryLabel);
        TimeSeries<Double> summedWeightHistory = ngm.summedWeightHistory(words,
                startYear, endYear);
        plotTS(summedWeightHistory, "Popularity", "year", "weight",
                categoryLabel);
    }

    /**
     * Creates a plot of the absolute word counts for WORD from STARTYEAR to
     * ENDYEAR, using NGM as a data source.
     * 
     * @param ngm
     * @param word
     * @param startYear
     * @param endYear
     */
    public static void plotCountHistory(NGramMap ngm, String word,
            int startYear, int endYear) {
        TimeSeries<Integer> countHistory = ngm.countHistory(word, startYear,
                endYear);
        plotTS(countHistory, "Popularity", "year", "count", word);
    }

    /**
     * Creates a plot of the processed history from STARTYEAR to ENDYEAR, using
     * NGM as a data source, and the YRP as a yearly record processor.
     * 
     * @param ngm
     * @param startYear
     * @param endYear
     * @param yrp
     */
    public static void plotProcessedHistory(NGramMap ngm, int startYear,
            int endYear, YearlyRecordProcessor yrp) {
        TimeSeries<Double> wordWeights = ngm.processedHistory(startYear,
                endYear, yrp);
        plotTS(wordWeights, "Word Length", "avg. length", "year", "word length");
    }

    /**
     * Creates a plot of the TimeSeries TS.
     * 
     * @param ts
     * @param title
     * @param xlabel
     * @param ylabel
     * @param legend
     */
    public static void plotTS(TimeSeries<? extends Number> ts, String title,
            String xlabel, String ylabel, String legend) {
        Collection<Number> years = ts.years();
        Collection<Number> counts = ts.data();

        // Create Chart
        Chart chart = QuickChart.getChart(title, ylabel, xlabel, legend, years,
                counts);

        // Show it
        new SwingWrapper(chart).displayChart();
    }

    /**
     * Creates a plot of the normalized weight counts for WORD from STARTYEAR to
     * ENDYEAR, using NGM as a data source.
     * 
     * @param ngm
     * @param word
     * @param startYear
     * @param endYear
     */
    public static void plotWeightHistory(NGramMap ngm, String word,
            int startYear, int endYear) {
        TimeSeries<Double> weightHistory = ngm.weightHistory(word, startYear,
                endYear);
        plotTS(weightHistory, "Popularity", "year", "weight", word);
    }

    private static Collection<Integer> downRange(int max) {
        ArrayList<Integer> ranks = new ArrayList<Integer>();
        for (int i = max; i >= 1; i -= 1) {
            ranks.add(i);
        }
        return ranks;
    }

    /**
     * Plots the normalized count of every word against the rank of every word
     * on a log-log plot.
     * 
     * @param ngm
     * @param year
     */
    public static void plotZipfsLaw(NGramMap ngm, int year) {
        YearlyRecord yr = ngm.getRecord(year);
        Collection<Number> counts = yr.counts();
        Collection<Integer> ranks = downRange(counts.size());

        Chart chart = new ChartBuilder().width(800).height(600)
                .xAxisTitle("rank").yAxisTitle("count").build();
        chart.getStyleManager().setYAxisLogarithmic(true);
        chart.getStyleManager().setXAxisLogarithmic(true);
        chart.addSeries("zipf", ranks, counts);
        new SwingWrapper(chart).displayChart();
    }

}