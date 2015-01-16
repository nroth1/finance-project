package feeder;

import java.util.*;

/**
 * Factory for initializing and returning all Feeders
 * 
 * @author Conor
 *
 */
public class FeederFactory {

    /** File Feeder */
    private static IFeeder _fileFeeder;

    /** Json Feeder */
    private static IFeeder _jsonFeeder;

    /** Currency Exchange Feeder */
    private static IFeeder _currencyExchangeFeeder;

    /**
     * Protected constructor for FeederFactory
     * 
     * @throws IllegalStateException
     */
    protected FeederFactory() {
        // Should never be instantiated
        throw new IllegalStateException();
    }

    /**
     * Get all of the feeders
     * 
     * @return all of the feeders
     */
    public static Set<IFeeder> GetAllFeeders() {
        initAllFeeders();
        Set<IFeeder> feeders = new HashSet<IFeeder>();
        // feeders.add(_fileFeeder);
        // feeders.add(_jsonFeeder);
        feeders.add(_currencyExchangeFeeder);
        // TODO: Add new feeders here

        return feeders;
    }

    /**
     * Getter for File Feeder
     * 
     * @return File Feeder
     */
    public static IFeeder getFileFeeder() {
        return _fileFeeder;
    }

    /**
     * Getter for Json Feeder
     * 
     * @return Json Feeder
     */
    public static IFeeder getJsonFeeder() {
        return _jsonFeeder;
    }

    /**
     * Getter for Currency Exchange Feeder
     * 
     * @return Currency Exchange Feeder
     */
    public static IFeeder getCurrencyExchangeFeeder() {
        return _currencyExchangeFeeder;
    }

    /**
     * Initialize all feeders
     */
    protected static void initAllFeeders() {
        // _fileFeeder = initFileFeeder();
        // _jsonFeeder = initJsonFeeder();
        _currencyExchangeFeeder = initCurrencyExchangeFeeder();
    }

    // /**
    // * Initialize the File Feeder
    // *
    // * @return a new File Feeder
    // */
    // protected static IFeeder initFileFeeder() {
    // return new FileFeeder();
    // }
    //
    // /**
    // * Initialize a Json Feeder
    // *
    // * @return A new Json Feeder
    // */
    // protected static IFeeder initJsonFeeder() {
    // // TODO: Put in Spring
    // final String urlString = "https://finance.google.com/finance/info?client=ig&q=NASDAQ%3a";
    // final String userAgent = "Mozilla/5.0";
    // final String feederName = "Json HTTP Request";
    // final String fileToReadFrom = "All-NYSE-Symbols.txt";
    //
    // return new FullPositionFeeder(urlString, userAgent, feederName, fileToReadFrom);
    // }

    /**
     * Initialize a Currency Exchange Feeder
     * 
     * @return a new Currency Exchange Feeder
     */
    protected static IFeeder initCurrencyExchangeFeeder() {
        // TODO: Put in Spring
        final String urlString = "http://rate-exchange.appspot.com/currency?from=";
        final String userAgent = "Mozilla/5.0";
        final String feederName = "Currency Exchange HTTP Request";
        final String fileToReadFrom = "Curencies-To-Query.txt";

        return new CurrencyExchangeFeeder(urlString, userAgent, feederName, fileToReadFrom);
    }
}
