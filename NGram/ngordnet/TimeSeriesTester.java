package ngordnet;
import java.util.Set;
import org.junit.Test;
import java.util.Collection;

import static org.junit.Assert.*;

public class TimeSeriesTester {
    @Test
    public void testData(){
    	TimeSeries<Double> ts = new TimeSeries<Double>();
    	ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        Collection<Number> data = ts.data();
        assertEquals(true, data.contains(3.6));
    }
    @Test
    public void testYear(){
    	TimeSeries<Double> ts = new TimeSeries<Double>();
    	ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        Collection<Number> years = ts.years();
        assertEquals(true, years.contains(1992));
    }
    
    @Test
    public void testCopyAll(){
    	TimeSeries<Double> ts = new TimeSeries<Double>();
    	ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        TimeSeries<Double> ts_copy = new TimeSeries<Double>(ts);
        assertEquals(true, ts.get(1992)==ts_copy.get(1992));
    }
    @Test
    public void testCopySome(){
    	TimeSeries<Double> ts = new TimeSeries<Double>();
    	ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        TimeSeries<Double> ts_copy = new TimeSeries<Double>(ts, 1993, 1995);
        assertEquals(true, ts.get(1993)==ts_copy.get(1993));
        assertEquals(true, ts.get(1995)==ts_copy.get(1995));
        assertEquals(false, ts.get(1992)==ts_copy.get(1992));
    }
    
    @Test
    public void testPlus(){
    	TimeSeries<Double> ts = new TimeSeries<Double>();
    	ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        TimeSeries<Integer> ts2 = new TimeSeries<Integer>();

        TimeSeries<Double> tSum = ts.plus(ts2);
        assertEquals((Double) 3.6, tSum.get(1992));
        assertEquals((Double)(-15.7), tSum.get(1996));
    }
    @Test
    public void testPlusEmpty(){
        TimeSeries<Double> ts = new TimeSeries<Double>();
        ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);
        TimeSeries<Integer> ts2 = new TimeSeries<Integer>();
        ts2.put(1991, 10);
        ts2.put(1992, -5);
        ts2.put(1993, 1);

        TimeSeries<Double> tSum = ts.plus(ts2);
        assertEquals((Double) 10.0, tSum.get(1991));
        assertEquals((Double)(-1.4), tSum.get(1992));
    }
    @Test
    public void testDivide(){
    	TimeSeries<Integer> ts2 = new TimeSeries<Integer>();
        ts2.put(1991, 10);
        ts2.put(1992, -5);
        ts2.put(1993, 1);
        TimeSeries<Double> ts3 = new TimeSeries<Double>();
        ts3.put(1991, 5.0);
        ts3.put(1992, 1.0);
        ts3.put(1993, 100.0);
        TimeSeries<Double> tQuotient = ts2.dividedBy(ts3);
        assertEquals((Double) 2.0, tQuotient.get(1991));
        assertEquals((Double)(-5.0), tQuotient.get(1992));
    }
    @Test
    public void testDivideNotEqualSize(){
        TimeSeries<Integer> ts2 = new TimeSeries<Integer>();
        
        TimeSeries<Double> ts3 = new TimeSeries<Double>();
        ts3.put(1991, 5.0);
        ts3.put(1992, 1.0);
        ts3.put(1993, 100.0);
        TimeSeries<Double> tQuotient = ts2.dividedBy(ts3);
        assertEquals(null, tQuotient.get(1991));
        assertEquals(null, tQuotient.get(1992));
    }
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TimeSeriesTester.class);
       
    }

}
