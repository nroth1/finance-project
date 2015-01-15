package main;

import java.util.*;

import feeder.*;

/**
 * 
 * @author Conor //
 * 
 *         TODO: Override Equals, HashCode, and ToString for each class - Using equals builder and hashcode builder
 * 
 *         TODO: Integrate Maven
 * 
 *         TODO: Integrate slf4j logging with logback - had classpath/config issues and gave up
 * 
 *         TODO: Move Currencies-To-Query.txt and All-NYSE-Symbols.txt into properties files
 * 
 *         TODO: Move general variables into properties files
 * 
 *         TODO: Have writing from feeders go to somewhere on the C drive instead of being project specific
 * 
 *         TODO: Integrate Java Cucumber for code coverage (haven't used this before but I've heard great things and I'd
 *         like to try it out)
 * 
 *         TODO: Use "Currency-Names" to print out the actual currency names of the 3 letter abbreviations
 * 
 *         TODO: Make the currency feed / analysis programs more self contained so they can be built on their own and
 *         then executed from cron jobs
 * 
 */
public class DCASMain {

    // public static Logger LOGGER = LoggerFactory.getLogger(DCMFMain.class);

    /**
     * Start the program
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting all feeders...");
        final long startFeeders = System.currentTimeMillis();
        Set<IFeeder> feeders = FeederFactory.GetAllFeeders();
        for (IFeeder feeder : feeders) {
            feeder.feed();
        }
        // LOGGER.info("hi");
        System.out.println();
        System.out.println("All feeders complete - " + (System.currentTimeMillis() - startFeeders) + " ms total");
    }

    /**
     * Overide default constructor to remove instantiability
     */
    private DCASMain() {
        throw new IllegalStateException();
    }

}
