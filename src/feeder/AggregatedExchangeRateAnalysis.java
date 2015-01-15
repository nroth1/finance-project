package feeder;

import java.util.*;

import feeder.CurrencyExchangeFeeder.ExchangeRate;

/**
 * A class that holds the information on currency exchange information over the course of the start to end date. This
 * data is used to run an aggregate analysis.
 * 
 * @author Conor
 *
 */
public class AggregatedExchangeRateAnalysis {

    Date _startDate;

    Date _endDate;

    Set<ExchangeRate> _analyzedExchangeRates;

    int num;

    /**
     * 
     */
    public AggregatedExchangeRateAnalysis() {
        _analyzedExchangeRates = new HashSet<ExchangeRate>();
    }

    /**
     * 
     */
    public void runAnalysis(Set<ExchangeRate> exchangeRates_) {
        System.out.println("Starting to run analysis on " + exchangeRates_.size() + " exchange rates...");

        for (ExchangeRate exchangeRate : exchangeRates_) {
            // _analyzedExchangeRates.add(exchangeRate);
        }
        num = exchangeRates_.size();
        System.out.println("Finished running analysis on " + exchangeRates_.size()
                + " exchange rates. Took TODO ms total.");
    }

    /**
     * 
     * @author Conor
     *
     */
    protected class AnalyzedExchangeRate {

        String _symbol;

    }

}
