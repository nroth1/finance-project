package feeder;

import java.util.*;

import feeder.CurrencyExchangeFeeder.ExchangeRate;

/**
 * A class that holds the information on currency exchange information over the course of the start to end date. This
 * data is used to run an aggregate analysis.
 * 
 * @author Conor
 *
 *         TODO: Does this class's name accurately reflect what it does? I don't really think so
 *
 */
public class AggregatedExchangeRateAnalysis {

    protected Date _startDate;

    protected Date _endDate;

    protected Set<AnalyzedExchangeRate> _analyzedExchangeRates;

    /**
     * Create a new instance of an AggregatedExchangeRateAnalysis
     */
    public AggregatedExchangeRateAnalysis() {
        _analyzedExchangeRates = new HashSet<AnalyzedExchangeRate>();
    }

    /**
     * Run the analysis on the give set of exchange rates
     * 
     * TODO: Double check to make sure all of the passed in exchange rates are actually within the start and end dates
     * of this instance
     */
    public void runAnalysis(Set<ExchangeRate> exchangeRates_) {
        System.out.println("Starting to run analysis on " + exchangeRates_.size() + " exchange rates...");

        // Separate the exchange rates by their from currency
        Map<String, Set<ExchangeRate>> ratesByCurrency = new HashMap<String, Set<ExchangeRate>>();

        for (ExchangeRate exchangeRate : exchangeRates_) {
            if (!(ratesByCurrency.containsKey(exchangeRate.from))) {
                ratesByCurrency.put(exchangeRate.from, new HashSet<ExchangeRate>());
            }
            ratesByCurrency.get(exchangeRate.from).add(exchangeRate);
        }

        for (String currencyFrom : ratesByCurrency.keySet()) {

            // TODO: Better named variables, some of these are getting a bit long and messy and narsty

            Map<String, Set<Date>> datesOfExchangeRates = new HashMap<String, Set<Date>>();

            List<ExchangeRate> exchangeRatesForThisCurrency = new ArrayList<ExchangeRate>(
                    ratesByCurrency.get(currencyFrom));

            Comparator<ExchangeRate> exchangeRateComparator = new Comparator<ExchangeRate>() {
                @Override
                public int compare(ExchangeRate ex1, ExchangeRate ex2) {
                    return ex1.date.compareTo(ex2.date);
                }
            };

            // The exchange rates are all now sorted by date
            Collections.sort(exchangeRatesForThisCurrency, exchangeRateComparator);

            // Create a mapping of the currencies that this currency has been compared to and their rates -- these are
            // already sorted by date
            Map<String, List<ExchangeRate>> separatedByCurrency = new HashMap<String, List<ExchangeRate>>();

            // Separate the exchange rates that this currency has been compared to by each currency
            for (ExchangeRate exchangeRate : exchangeRatesForThisCurrency) {
                if (!(separatedByCurrency.containsKey(exchangeRate.to))) {
                    separatedByCurrency.put(exchangeRate.to, new ArrayList<ExchangeRate>());
                }
                separatedByCurrency.get(exchangeRate.to).add(exchangeRate);
            }

            // TODO: There's certainly a safer way than just making these all 0's, just not doing it right now.
            // TODO: God this is fucking ugly. Just making things work right now thought just to see how it works / do a
            // demo for Olo

            // TODO : Calculate these
            double aggregatePercentIncrease = 0;
            double dailyAggregatePercentIncrease = 0;
            double percentDailyIncreaseChance = 0;

            // This is a map of String -> ExchangeRates that are the "To" part of a currency exchange under the current
            // currency in this iteration
            for (String currencyTo : separatedByCurrency.keySet()) {
                for (ExchangeRate exchangeRate : separatedByCurrency.get(currencyTo)) {
                    if (!(datesOfExchangeRates.containsKey(exchangeRate.to))) {
                        datesOfExchangeRates.put(exchangeRate.to, new HashSet<Date>());
                    }
                    datesOfExchangeRates.get(exchangeRate.to).add(exchangeRate.date);

                }
            }
            // Create a new AnalyzedExchangeRate and add it to the list of analyzed exchange rates
            AnalyzedExchangeRate analyzedExchangeRate = new AnalyzedExchangeRate(currencyFrom, _startDate, _endDate,
                    datesOfExchangeRates, aggregatePercentIncrease, dailyAggregatePercentIncrease,
                    percentDailyIncreaseChance);
            _analyzedExchangeRates.add(analyzedExchangeRate);
        }

        // TODO: Add time logging
        System.out.println("Finished running analysis on " + exchangeRates_.size()
                + " exchange rates. Took TODO ms total.");
    }

    /**
     * A class to hold an analysis of a currency and it's exchange rates to other currencies from a start to an end date
     * 
     * @author Conor
     * 
     *         TODO: Comment everything
     * 
     *         TODO: Put in another file (?)
     *
     */
    protected class AnalyzedExchangeRate {

        protected String _symbol;

        protected Date _startDate;

        protected Date _endDate;

        protected Map<String, Set<Date>> _datesOfExchanges;

        protected double _aggregatePercentIncrease;

        protected double _aggregateDailyPercentIncrease;

        protected double _percentOfDailyIncreases;

        /**
         * 
         * @param symbol_
         * @param startDate_
         * @param endDate_
         */
        protected AnalyzedExchangeRate(final String symbol_, final Date startDate_, final Date endDate_,
                final Map<String, Set<Date>> datesOfExchanges_, final double aggregatePercentIncrease_,
                final double aggregateDailyPercentIncrease_, final double percentOfDailyIncreases_) {
            _symbol = symbol_;
            _startDate = startDate_;
            _endDate = endDate_;
            _datesOfExchanges = datesOfExchanges_;
            _aggregatePercentIncrease = aggregatePercentIncrease_;
            _aggregateDailyPercentIncrease = aggregateDailyPercentIncrease_;
            _percentOfDailyIncreases = percentOfDailyIncreases_;
        }
    }

}
