package feeder;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.lang3.builder.*;

import com.google.gson.*;

/**
 * A feeder to get currency exchange rates and print them out as JSON to a text file
 * 
 * @author Conor
 *
 */
public class CurrencyExchangeFeeder extends AUrlRequestFeeder {

    /** The list of exchange rates that will be populated by the responses from the HTTP requests */
    List<ExchangeRate> _exchangeRates = new ArrayList<ExchangeRate>();

    /**
     * Protected constructor to create a GoogleCurrencyExchangeFeeder
     * 
     * @param urlString_
     *            The base URL String that we will submit queries to
     * @param userAgent_
     *            What type of web browser is submitting the request
     * @param feederType_
     *            What type of feeder this is (used for logging purposes)
     * @param fileToReadFrom_
     *            What file we want to read
     */
    public CurrencyExchangeFeeder(final String urlString_, final String userAgent_, final String feederType_,
            final String fileToReadFrom_) {
        super(urlString_, userAgent_, feederType_, fileToReadFrom_);
    }

    @Override
    protected void queryUrlService() {
        // Create a mappign to pair all of the symbols that this class was initialized with
        Map<String, Set<String>> pairedSymbols = pairSymbols(_symbols);

        // For every combination of symbols, query the URL that we create for that combination.
        for (String key : pairedSymbols.keySet()) {
            for (String value : pairedSymbols.get(key)) {
                try {
                    submitRequest(createUrlToQuery(key, value));
                } catch (Exception ex) {
                    // TODO: Make a real logging statement
                    System.out.println("Caught some shit, skipping some shit");
                }
            }
        }
    }

    /**
     * Create the url to get the exchange rate from the first given symbol to the second given symbol
     * 
     * @param from_
     *            The name of the currency we're comparing from
     * @param to_
     *            The name of the currency we're comparing to
     * @return The string that we will use to submit an HTTP GET request
     */
    protected String createUrlToQuery(final String from_, final String to_) {
        String tempUrlString = _urlString;
        tempUrlString = tempUrlString + from_;
        tempUrlString = tempUrlString + "&to=";
        tempUrlString = tempUrlString + to_;
        return tempUrlString;
    }

    /**
     * Create all possible pairings of the symbols that are passed in
     * 
     * @param symbols_
     *            Symbols to pair
     * @return All possible pairings of the symbols
     */
    protected Map<String, Set<String>> pairSymbols(Set<String> symbols_) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        for (String symbol1 : symbols_) {
            for (String symbol2 : symbols_) {
                if (!result.containsKey(symbol1)) {
                    result.put(symbol1, new HashSet<String>());
                }
                result.get(symbol1).add(symbol2);
            }
        }

        // Remove pairings of the same symbols
        for (String key : result.keySet()) {
            if (result.get(key).contains(key)) {
                result.get(key).remove(key);
            }
        }

        System.out.println("Created lists of currencies to compare:");
        for (String s : result.keySet()) {
            System.out.println(s + " :: " + result.get(s));
        }

        return result;
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

            // TODO: Put folder / file names as variables
            final String fileName = String.format("Currency-Exchange/Raw-Data/Currency-Exchange-Rates-%S",
                    dateFormat.format(date));
            System.out.println("Writing to file: " + fileName + "...");

            writer = new PrintWriter(fileName, "UTF-8");
            writer.println("Aggregate Exchange Rates");
            writer.println("--------------------------");
            writer.println("SYM|AGGREGATE|AVERAGE");
            writer.println("--------------------------");
            // TODO: Put the aggregation in a different method, this is too long
            Set<String> currencies = new HashSet<String>();
            for (ExchangeRate rate : _exchangeRates) {
                if (!currencies.contains(rate.from)) {
                    currencies.add(rate.from);
                }
            }
            // Round the decimal to 6 places
            // TODO: Put this in a static final variable somewhere
            // TODO: Also, is this the best way to do this?
            DecimalFormat df = new DecimalFormat("#.######");
            for (String currency : currencies) {
                double aggregatedRate = 0.0;
                int counter = 0;
                for (ExchangeRate rate : _exchangeRates) {
                    if (rate.from.equals(currency)) {
                        aggregatedRate += rate.rate;
                        counter++;
                    }
                }
                writer.println(String.format("%S|%S|%S", currency, df.format(aggregatedRate),
                        df.format(aggregatedRate / counter)));
            }
            writer.println("--------------------------");
            // End of aggregation
            for (ExchangeRate rate : _exchangeRates) {
                Gson gson = new Gson();
                String line = gson.toJson(rate);
                // System.out.println(line);
                writer.println(line);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("Finished writing contents to file - " + (System.currentTimeMillis() - startWrite)
                + " ms total.");
    }

    @Override
    protected void storeResponse(String response_) {
        // TODO: Add some checking to see if the response is in correct JSON format
        // Create a Gson to deserialize the given string
        Gson gson = new Gson();

        // Create an exchange by deserializing the given String response
        ExchangeRate exchange = gson.fromJson(response_, ExchangeRate.class);

        // Add the current date to the new ExchangeRate instance
        exchange.date = new Date();

        // TODO: Check if all of the fields of the exchange rate to make sure everything is valid
        _exchangeRates.add(exchange);
    }

    /**
     * A concrete class to represent an exchange rate from one currency to another
     */
    protected class ExchangeRate {

        /** The currency that we're comparing FROM */
        protected String from;

        /** The currency that we're comparing TO */
        protected String to;

        /** The rate of the currency exchange */
        protected double rate;

        /** The date of the comparison */
        protected Date date;

        // TODO: Should this have a constructor? Gson doesn't require one, would throwing an IllegalStateException
        // prevent us from creating this object except for when we deserialize from JSON String?

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("from", from).append("to", to).append("rate", rate)
                    .append("date", date).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof ExchangeRate) {
                ExchangeRate temp = (ExchangeRate) o;

                return new EqualsBuilder().append(from, temp.from).append(to, temp.to).append(rate, temp.rate)
                        .append(date, temp.date).isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            // TODO: Use hashcodebuilder
            return 0;
        }
    }

}
