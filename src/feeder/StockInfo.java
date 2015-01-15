package feeder;
import java.math.*;
import java.text.*;
import java.util.*;


/**
 * A class that holds a day's worth of information about a stock
 * 
 * @author Conor
 *
 */
public class StockInfo {

    protected String _symbol;
    protected Date _date;
    protected double _dayOpen;
    protected double _dayClose;
    protected double _dayHigh;
    protected double _dayLow;
    protected BigInteger _volume;

    public static StockInfo newInstance(String[] splitString) {
        return new StockInfo(splitString);
    }

    private StockInfo(String[] splitString_) {
        try {
            init(splitString_);
        }
        catch (Exception e) {
        }
    }

    /**
     * Initialize this StockInfo class
     * 
     * @param splitString_
     * @throws Exception
     */
    protected void init(String[] splitString_) throws Exception {
        if (splitString_.length == 7) {
            _symbol = splitString_[0];
            _date = createSimpleDate(splitString_[1]);
            _dayOpen = Double.parseDouble(splitString_[2]);
            _dayHigh = Double.parseDouble(splitString_[3]);
            _dayLow = Double.parseDouble(splitString_[4]);
            _dayClose = Double.parseDouble(splitString_[5]);
            _volume = new BigInteger(splitString_[6]);
        }
        else {
            throw new Exception("Shit nahhhh fugg datttttt");
        }
    }

    /**
     * 
     * @param dateString
     * @return
     */
    protected Date createSimpleDate(String dateString) {
        Date simpleDate;
        try {
            String dateFormat = "yyyyMMdd";
            simpleDate = new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateString);
        }
        catch (ParseException e) {
            // log error
            e.printStackTrace();
            // TODO: Don't return null
            return null;
        }
        return simpleDate;
    }
}
