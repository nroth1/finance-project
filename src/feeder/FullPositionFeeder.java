package feeder;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import com.google.gson.*;

/**
 * 
 * @author Conor
 *
 */
public class FullPositionFeeder extends AUrlRequestFeeder {

    List<StockPosition> _positions = new ArrayList<StockPosition>();

    /**
     * 
     * @param urlString_
     * @param userAgent_
     * @param feederType_
     * @param fileToReadFrom_
     */
    public FullPositionFeeder(final String urlString_, final String userAgent_, final String feederType_,
            final String fileToReadFrom_) {
        super(urlString_, userAgent_, feederType_, fileToReadFrom_);
    }

    /**
     * Partition a set of symbols into sets
     * 
     * @param setToSplit
     * @return
     */
    protected <T> List<Set<T>> partitionSet(Set<T> setToSplit, int numberPerPartition) {
        List<Set<T>> result = new ArrayList<Set<T>>();

        Iterator<T> it = setToSplit.iterator();

        while (it.hasNext()) {
            Set<T> partition = new HashSet<T>();

            for (int i = 0; i < numberPerPartition; i++) {
                try {
                    T t = it.next();
                    partition.add(t);
                } catch (Exception e) {
                    // System.out.println(e);
                }
            }

            result.add(partition);
            // System.out.println("Created partition of " + numberPerPartition);
        }

        return result;
    }

    @Override
    protected void queryUrlService() {
        // Create partitions of the symbols that this class was initialized with in order to submit batch requests to
        // the URL service
        List<Set<String>> batchesOfSymbols = partitionSet(_symbols, 300);

        // Create
        for (Set<String> symbols : batchesOfSymbols) {
            if (symbols != null) {
                if (symbols.size() > 0) {
                    submitRequest(createUrlToQuery(symbols));
                }
            }
        }
    }

    // TODO: Should this be an abstract? EHhhhhh kinda weird how this is now -- Implemented in both classes
    // TODO: Maybe combine with query Url Service?
    protected String createUrlToQuery(Set<String> symbols_) {
        String tempUrlString = _urlString;
        for (String symbol : symbols_) {
            tempUrlString = tempUrlString + symbol;
            tempUrlString = tempUrlString + ",";
        }
        return tempUrlString;
    }

    @Override
    public void write() {

        System.out.println("Starting to write contents to file...");
        final long startWrite = System.currentTimeMillis();
        PrintWriter writer;

        try {
            System.out.println("Printing all symbols to a text file");

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH mm ss");
            Date date = new Date();

            final String fileName = String.format("Full-Position/Full-Position-%S", dateFormat.format(date));
            System.out.println("Writing to file: " + fileName + "...");

            // TODO: Put UTF-8 in a properties file somewhere
            writer = new PrintWriter(fileName, "UTF-8");
            for (StockPosition position : _positions) {
                Gson gson = new Gson();
                String line = gson.toJson(position);
                writer.println(line);
            }
            writer.close();

            System.out.println("Finished writing contents to file " + fileName + " - "
                    + (System.currentTimeMillis() - startWrite) + " ms total.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void storeResponse(String response_) {
        String cleanResponse = response_.substring(response_.indexOf("["));
        String regex = "(?<=[\\d])(,)(?=[\\d])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cleanResponse);
        cleanResponse = m.replaceAll("");
        // cleanResponse.replaceAll("\"\"", "\"0\"");
        // TODO: Find a way to discard the String if it comes back with only empty strings, or incorrect types. The
        // problem with this is that obviously type checking is a bit harder before the fields are in the actual object.
        // Alternatively, we could just create the object no matter the fields of the String that is passed in are, and
        // then to type checking and whatever afterwards (see TODO below).

        Gson gson = new Gson();
        StockPosition[] positions = gson.fromJson(cleanResponse, StockPosition[].class);

        // TODO: Possibly check all fields to make sure that the types are correct / etc...
        _positions.addAll(Arrays.asList(positions));
    }

    /**
     * 
     * @author Conor
     * 
     *         TODO: Anyway to dynamically allocate fields? Probs not but this is ugly as fuck
     * 
     *         TODO: Also, docs n shit
     * 
     */
    protected class StockPosition {

        // TODO: Make these actually reflect the types that they should be. Everything is only currently a String
        // because there were problems with some responses coming back with empty strings for the JSON fields
        protected BigInteger id;
        protected String t;
        protected String e;
        protected String l; // make double
        protected String l_fix; // Make double
        protected String l_cur; // make double
        protected String s;
        protected String ltt;
        protected String lt;
        protected String lt_dts;
        protected String c;
        protected String c_fix; // make double
        protected String cp; // make double
        protected String cp_fix; // make double
        protected String ccol; // make double
        protected String pcls_fix;

        /**
         * TODO: Javadoc TODO: The Constructor here is not used by Gson to create this object, would making this
         * constructor throw an IllegalStateException still allow Gson to create the object, but prevent us from
         * creating it? If so, that would probably be preferable.
         * 
         * 
         * @param id
         * @param t
         * @param e
         * @param l
         * @param l_fix
         * @param l_cur
         * @param s
         * @param ltt
         * @param lt
         * @param lt_dts
         * @param c
         * @param c_fix
         * @param cp
         * @param cp_fix
         * @param ccol
         * @param pcls_fix
         */
        protected StockPosition(final int id, final String t, final String e, final double l, final double l_fix,
                final double l_cur, final int s, final String ltt, final String lt, final String lt_dts,
                final String c, final double c_fix, final double cp, final double cp_fix, final String ccol,
                final double pcls_fix) {
        }
    }

}
