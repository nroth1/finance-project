package analysis;

import feeder.*;

/**
 * A class for running our analysis services
 * 
 * @author Conor
 *
 */
public class AnalysisService {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        CurrencyExchangeAnalyzer.startAnalysis();
    }

    /**
     * Prevent instantiability
     */
    public AnalysisService() {
        throw new IllegalStateException();
    }
}
