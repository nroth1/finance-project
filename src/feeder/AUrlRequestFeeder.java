package feeder;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A class to submit GET Requests to HTTP Services
 * 
 * @author Conor Ebbs
 *
 */
public abstract class AUrlRequestFeeder implements IFeeder {

    Set<String> _symbols;

    String _urlString;

    protected String _userAgent;

    protected String _feederType;

    protected String _fileToReadFrom;

    protected AUrlRequestFeeder(final String urlString_, final String userAgent_, final String feederName_,
            final String fileToReadFrom_) {
        _symbols = new HashSet<String>();
        _urlString = urlString_;
        _userAgent = userAgent_;
        _feederType = feederName_;
        _fileToReadFrom = fileToReadFrom_;
    }

    @Override
    public void feed() {
        System.out.println("Starting " + _feederType + " feeder");
        long startFeed = System.currentTimeMillis();

        readFromFile();
        queryUrlService();
        System.out.println("Json feed complete - " + (System.currentTimeMillis() - startFeed) + " ms total");
        write();
    }

    @Override
    public abstract void write();

    /**
     * Query the URL Service
     */
    protected abstract void queryUrlService();

    /**
     * Store the given string response as an actual java object
     * 
     * @param response_
     */
    protected abstract void storeResponse(String response_);

    /**
     * Submit a HTTP GET Request to the given URL
     * 
     * @param symbols
     * @return
     */
    // TODO: Thread this shit
    protected boolean submitRequest(final String urlToQuery) {
        // TODO: Change logging to be more accurate for inheriting classes

        try {

            // System.out.println(tempUrlString);\
            String tempUrlString = urlToQuery;
            tempUrlString = tempUrlString.substring(0, tempUrlString.length());
            URL url = new URL(tempUrlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", _userAgent);

            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // String jsonResponse = response.substring(response.indexOf("["));

            System.out.println("Response : " + response);
            if (responseCode == 200) {
                storeResponse(response.toString());
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println("IOException - BAD REQUEST - Probably a non-existent symbol.");
            // e.printStackTrace();
        }
        return false;
    }

    /**
     * Read symbols from a file and store them in the class variable
     */
    protected void readFromFile() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(_fileToReadFrom));
            String line = null;
            while ((line = reader.readLine()) != null) {
                _symbols.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
