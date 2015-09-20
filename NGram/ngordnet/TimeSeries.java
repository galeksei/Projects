package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 * @since 03/01/2015
 *
 */
public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {

    /**
     * Default constructor
     */
    public TimeSeries() {

    }

    /**
     * Creates a copy of TS.
     * 
     * @param ts
     *            TimeSeries to copy
     */
    public TimeSeries(TimeSeries<T> ts) {
        super(ts);
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR, inclusive
     * of both end points.
     * 
     * @param ts
     *            TimeSeries to copy
     * @param startYear
     *            the starting key
     * @param endYear
     *            the ending key
     */
    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        super(ts.subMap(startYear, true, endYear, true));
    }

    /**
     * Returns the sum of this time series with the given ts.
     * 
     * @param ts
     * @return a TimeSeries where each year has a sum as a value
     */
    public TimeSeries<Double> plus(TimeSeries<? extends Number> ts) {
        TimeSeries<Double> temp = new TimeSeries<Double>();
        Set<Integer> allYears = new HashSet<Integer>();
        allYears.addAll(this.keySet());
        allYears.addAll(ts.keySet());
        for (Integer x : allYears) {
            if (containsKey(x) && ts.containsKey(x)) {
                temp.put(x, get(x).doubleValue() + ts.get(x).doubleValue());
            } else if (containsKey(x) && !ts.containsKey(x)) {
                temp.put(x, get(x).doubleValue());
            } else if (!containsKey(x) && ts.containsKey(x)) {
                temp.put(x, ts.get(x).doubleValue());
            }
        }
        return temp;
    }

    /**
     * Returns the quotient of this time series divided by the relevant value in
     * ts.
     * 
     * @param ts
     *            : a TimmeSeries to divide out each year by
     * @return a TimeSeries that has quotient as the values
     */
    public TimeSeries<Double> dividedBy(TimeSeries<? extends Number> ts) {
        if (ts == null) {
            throw new IllegalArgumentException();
        }
        Set<Integer> allYears = new HashSet<Integer>();
        allYears.addAll(this.keySet());
        TimeSeries<Double> temp = new TimeSeries<Double>();
        for (Integer x : allYears) {
            if ((containsKey(x) && !ts.containsKey(x))) {
                throw new IllegalArgumentException();           
            } else {
                temp.put(x, get(x).doubleValue() / ts.get(x).doubleValue());
            }
        }
        return temp;
    }

    /**
     * 
     * @return arrayList with all the keys (years)
     */
    public Collection<Number> years() {
        ArrayList<Number> temp = new ArrayList<Number>();
        Collection<Integer> integerSet = keySet();
        for (Number x : integerSet) {
            temp.add(x);
        }
        return temp;
    }

    /**
     * 
     * @return arrayList with all the values
     */
    public Collection<Number> data() {
        ArrayList<Number> temp = new ArrayList<Number>();
        Collection<T> dataSet = values();
        for (Number x : dataSet) {
            temp.add(x);
        }
        return temp;
    }
}
