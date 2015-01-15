package feeder;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;

import com.google.gson.*;

import feeder.CurrencyExchangeFeeder.ExchangeRate;

/**
 * A class to read all of the files that contain the currency exchange data and run analysis on them
 * 
 * @author Conor
 * 
 *         TODO: Should everything be static? This class is currently never intitialzed so it makes sense. But what are
 *         the possible implications of this?
 *
 */
public class CurrencyExchangeAnalyzer {

    /** List of all of the exchange rates that are loaded up from the currency exchange files */
    protected static List<ExchangeRate> _exchangeRates = new ArrayList<ExchangeRate>();

    /**
     * Remove instantiability
     */
    public CurrencyExchangeAnalyzer() {
        throw new IllegalStateException();
    }

    /**
     * Start the analysis
     * 
     * @param args
     */
    public static void startAnalysis() {
        readRawDataFromFiles();
        aggregateRawData();
        runAnalysis();
    }

    /**
     * Read fromt the files that contain the currency exchange data
     */
    protected static void readRawDataFromFiles() {
        File dir = new File("./Currency-Exchange/Raw-Data");

        FileFilter textFileFilter_ = new WildcardFileFilter("Currency-Exchange-Rates-*");

        File[] textFiles_ = dir.listFiles(textFileFilter_);
        for (File file : textFiles_) {
            readFrom(file);
        }
    }

    /**
     * Reads from the given file and adds all of the exchange rates to the list of exchange rates
     * 
     * @param file_
     */
    protected static void readFrom(File file_) {
        try {
            System.out.print("Reading from file: " + file_.getName() + "... ");
            BufferedReader reader = new BufferedReader(new FileReader(file_));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("{")) {
                    Gson gson = new Gson();
                    ExchangeRate newExchangeRate = gson.fromJson(line, ExchangeRate.class);
                    // TODO: Either don't create the "newExchangeRate" object and just add it to the list, OR actually
                    // check the newExchangeRate to make sure the object is valid
                    _exchangeRates.add(newExchangeRate);
                }
            }
            reader.close();
            System.out.println("Complete");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception ex) {
            // TODO: Add exception handling
            ex.printStackTrace();
        }
    }

    /**
     * Runs analysis on all of our raw data, assigns Currency Exchange Rate Scores (DCAS construct) to each one over a
     * 7-day period,
     */
    protected static void aggregateRawData() {
        // Create a set of dates which indicates which days we have already analyzed and aggregated our raw data
        // TODO: Should this be a set of actual dates? Alternatively, it could be a String regex'ed out from the file
        // name to then check against the file names of the
        Set<Date> analyzedData = getDatesOfAnalyzedData();

        // Create a list of exchange rates that we still have to analyze and aggregate
        Set<ExchangeRate> exchangeRatesToAggregate = new HashSet<ExchangeRate>();

        // For every exchange rate in raw data, if we haven't aggregated the analysis, aggregate the analysis
        for (ExchangeRate exchangeRate : _exchangeRates) {
            if (!(analyzedData.contains(exchangeRate.date))) {
                exchangeRatesToAggregate.add(exchangeRate);
            }
        }

        List<AggregatedExchangeRateAnalysis> aggregatedAnalyses = new ArrayList<AggregatedExchangeRateAnalysis>();
        Set<ExchangeRate> seenExchangeRates = new HashSet<ExchangeRate>();
        Set<ExchangeRate> exchangeRatesToAnalyze = new HashSet<ExchangeRate>();

        for (ExchangeRate rateToAggregate : exchangeRatesToAggregate) {
            if (!(seenExchangeRates.contains(rateToAggregate))) {
                AggregatedExchangeRateAnalysis aggregation = new AggregatedExchangeRateAnalysis();

                // Set the date of this exchange rate as the end date for this aggregation
                aggregation._endDate = rateToAggregate.date;

                Calendar cal = Calendar.getInstance();
                cal.setTime(aggregation._endDate);
                cal.add(Calendar.HOUR, (-(7 * 24)));
                Date startDate = cal.getTime();

                // Set the start date for this aggregation
                aggregation._startDate = startDate;

                // For every exchange rate that we have loaded in memory, if it's between the start and end date of the
                // AggregatedExchangeRateAnalysis object, then add it to a list of exchange rates that we want to
                // analyze.
                for (ExchangeRate exchangeRate : _exchangeRates) {
                    if ((exchangeRate.date.compareTo(aggregation._startDate) > 0)
                            && (exchangeRate.date.compareTo(aggregation._endDate) <= 0)) {
                        exchangeRatesToAnalyze.add(exchangeRate);
                        // TODO: Super messy and innefficient
                        seenExchangeRates.add(exchangeRate);
                    }
                }

                // Run the analysis for the exchange rates we want to analyze and store htem in the "aggregation" object
                aggregation.runAnalysis(exchangeRatesToAnalyze);

                // Add this aggregation to the list of aggregations that we will write to a file
                aggregatedAnalyses.add(aggregation);
            }
        }

        writeAggregationToFile(aggregatedAnalyses);

    }

    protected static void writeAggregationToFile(List<AggregatedExchangeRateAnalysis> aggregations_) {

        System.out.println("Starting to write contents to file...");
        System.out.println("Writing " + aggregations_.size() + " aggregations to file...");
        final long startWrite = System.currentTimeMillis();
        PrintWriter writer;

        try {
            System.out.println("Printing all symbols to a text file");
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH mm ss");
            Date date = new Date();

            // TODO: Put folder / file names as variables
            final String fileName = String.format("Currency-Exchange/Analysis-Aggregates/Analysis-Aggregates-%S",
                    dateFormat.format(date));
            System.out.println("Writing to file: " + fileName + "...");

            writer = new PrintWriter(fileName, "UTF-8");

            for (AggregatedExchangeRateAnalysis aggregation : aggregations_) {
                Gson gson = new Gson();
                String line = gson.toJson(aggregation);
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

    /**
     * TODO: Collapse with readRawDataFromFiles, they probably don't need to be separate methods
     * 
     * @return
     */
    protected static Set<Date> getDatesOfAnalyzedData() {
        Set<Date> analyzedData = new HashSet<Date>();

        File dir = new File("./Currency-Exchange/Analysis-Aggregates");

        FileFilter textFileFilter_ = new WildcardFileFilter("Aggregated-Analysis*");

        File[] textFiles = dir.listFiles(textFileFilter_);
        analyzedData.addAll(getDatesFrom(textFiles));
        return analyzedData;
    }

    /**
     * Get a Collection of dates from the raw currency exchange data in the given file
     * 
     * TODO: Collapse with readFrom
     * 
     * @param file_
     * @return
     */
    protected static Collection<Date> getDatesFrom(File... files_) {
        Set<Date> dates = new HashSet<Date>();

        for (File file : files_) {
            try {
                System.out.print("Reading from file: " + file.getName() + "... ");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    // TODO: Better way of making sure we're looking at a JSON String? Just checking for { kinda sucks
                    if (line.startsWith("{")) {
                        Gson gson = new Gson();
                        AggregatedExchangeRateAnalysis aggregatedExchangeRates = gson.fromJson(line,
                                AggregatedExchangeRateAnalysis.class);
                        // for (ExchangeRate exchangeRate : aggregatedExchangeRates._exchangeRates) {
                        // dates.add(exchangeRate.date);
                        // }
                    }
                }
                reader.close();
                System.out.println("Complete.");
            } catch (IOException e) {
                // TODO Log error / exception handling
                e.printStackTrace();
            } catch (Exception ex) {
                // TODO: Unexpected
                // TODO: Log error / exception handling
                ex.printStackTrace();
            }
        }

        return dates;
    }

    /**
     * Run the analysis on the data loaded from the files that contain the currency exchange data
     * 
     * TODO: Run analysis on a combination of raw data (for the current week) and aggregated analyzed data from past
     * history (x many years)
     * 
     * TODO: Split up into separate methods
     */
    public static void runAnalysis() {
        System.out.println();
        System.out.println("Running analysis...");
        System.out.println("-------------------");
        // TODO: Better variable name(s) --- Some of these suck / are misleading
        Map<String, List<ExchangeRate>> separatedByCurrency = new HashMap<String, List<ExchangeRate>>();

        // Separate all of the exchange rates that were read from the currency exchange files by FROM currency
        for (ExchangeRate rate : _exchangeRates) {
            if (!(separatedByCurrency.containsKey(rate.from))) {
                separatedByCurrency.put(rate.from, new ArrayList<ExchangeRate>());
            }
            separatedByCurrency.get(rate.from).add(rate);
        }
        // TODO: Make sure the lists of ExchangeRates are sorted by date

        DecimalFormat df = new DecimalFormat("#.######");

        for (String currency : separatedByCurrency.keySet()) {
            System.out.println(currency);
            Date oldestDate = new Date();
            List<String> comparedAgainst = new ArrayList<String>();
            double totalIncrease = separatedByCurrency.get(currency).get(0).rate;
            Map<String, List<Double>> exchangeRelationships = new HashMap<String, List<Double>>();
            for (ExchangeRate exchangeRate : separatedByCurrency.get(currency)) {

                //
                totalIncrease += exchangeRate.rate;

                //
                oldestDate = exchangeRate.date.compareTo(oldestDate) < 0 ? exchangeRate.date : oldestDate;

                //
                if (!(exchangeRelationships.containsKey(exchangeRate.to))) {
                    exchangeRelationships.put(exchangeRate.to, new ArrayList<Double>());
                }
                exchangeRelationships.get(exchangeRate.to).add(exchangeRate.rate);

                //
                if (!(comparedAgainst.contains(exchangeRate.to))) {
                    comparedAgainst.add(exchangeRate.to);
                }
            }
            System.out.println("Data contains exchange rates against: " + comparedAgainst);
            Date latestDate = oldestDate;
            for (ExchangeRate rate : separatedByCurrency.get(currency)) {
                if (rate.date.compareTo(latestDate) > 0) {
                    latestDate = rate.date;
                }
            }
            System.out.println("Oldest date: " + oldestDate);
            System.out.println("Latest date: " + latestDate);
            // TODO: Name stuff better, clean all of this up

            System.out.println("Average exchange rates (number of data points)");
            for (String s : exchangeRelationships.keySet()) {

                double total = 0;
                for (Double d : exchangeRelationships.get(s)) {
                    total += d;
                }
                System.out.println(s + " -- " + df.format((total / exchangeRelationships.get(s).size())) + " ("
                        + exchangeRelationships.get(s).size() + ")");

            }

            // TODO: This only shows a bad exchange rate on a single day, want aggregate worst relationship
            // System.out.println("Best exchange rate: " + bestExchange);
            // System.out.println("Worst exchange rate: " + worstExchange);
            double averageIncrease = totalIncrease / separatedByCurrency.get(currency).size();

            // System.out.println("Total aggregate exchange rate for " + currency + " ::: " + df.format(totalIncrease));
            System.out.println("Average aggregate exchange rate for " + currency + " ::: "
                    + df.format(averageIncrease));
            System.out.println("-------------------");
        }

        System.out.println("Finished running analysis...");
    }

}
