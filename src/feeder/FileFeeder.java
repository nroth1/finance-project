package feeder;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;

import cache.*;

import com.google.gson.*;

/**
 * File Feeder
 * 
 * @author Conor TODO: Make this a 7-Column-Metastock-File-Feeder
 */
public class FileFeeder implements IFeeder {

    // Logger LOGGER = LoggerFactory.getLogger(FileFeeder.class);

    private Map<File, Set<File>> _files = new HashMap<File, Set<File>>();

    // TODO: These are no longer really caches, we want them to be one per feeder, rename to make that relationship
    // clearer
    Cache<String, Set<StockInfo>> _stockCache = StockCache.newInstance();

    /**
     * Creating an instance of this FileFeeder will automatically load from the Folders under the Data Folder
     */
    public FileFeeder() {
        _files = loadFromFiles();
    }

    /**
     * Start the feed for this FileFeeder
     */
    @Override
    public void feed() {
        System.out.println("Starting File Feeder...");
        final long startFeeder = System.currentTimeMillis();
        if (_files != null) {
            if (_files.size() > 0) {
                // TODO: double check the logic here and make it easier to follow
                feedFrom(_files);
            }
        }
        System.out.println("File feed complete - " + (System.currentTimeMillis() - startFeeder) + " ms total");
        write();
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

            String fileName = String.format("7-Col-Metastock/7-Col-Metastock-%S", dateFormat.format(date));
            System.out.println("Writing to file: " + fileName + "...");

            writer = new PrintWriter(fileName, "UTF-8");
            for (String key : _stockCache.keySet()) {
                for (StockInfo info : _stockCache.get(key)) {
                    Gson gson = new Gson();
                    final String line = gson.toJson(info);
                    writer.println(line);
                }
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
     * Feed from the given File
     * 
     * @param file
     * @return
     */
    protected boolean feedFrom(Map<File, Set<File>> folderToFileMap) {
        // LOGGER.info("Feeding from {} files...", files.size());
        boolean feedFromFilesSuccessful = false;

        for (File folder : folderToFileMap.keySet()) {
            Set<File> files = new HashSet<File>(folderToFileMap.get(folder));
            System.out.print("Feeding from " + files.size() + " " + folder.getName() + " files... ");

            for (File file : files) {
                // System.out.println("Feeding from: " + file.getName());
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String splitter = line.contains(",") ? "," : "|";
                        String[] splitLine = line.split(splitter);
                        String symbol = splitLine[0];
                        if (!_stockCache.containsKey(symbol)) {
                            _stockCache.put(symbol, new HashSet<StockInfo>());
                        }
                        // System.out.println(symbol);
                        _stockCache.get(symbol).add(StockInfo.newInstance(splitLine));
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            System.out.println("Complete.");
        }
        return feedFromFilesSuccessful;
    }

    /**
     * TODO: Make this less awful TODO: Make the /Data from a properties file
     */
    protected Map<File, Set<File>> loadFromFiles() {
        Map<File, Set<File>> folderToFileMap = new HashMap<File, Set<File>>();
        File dir = new File("./Data");

        FileFilter exchangeFolderFilter_ = new WildcardFileFilter("*");
        File[] exchangeFolders_ = dir.listFiles(exchangeFolderFilter_);

        FileFilter textFileFilter_ = new WildcardFileFilter("*.txt");

        for (File folder_ : exchangeFolders_) {
            File[] textFiles_ = folder_.listFiles(textFileFilter_);
            Set<File> filesInFolder = new HashSet<File>();
            for (File file : textFiles_) {
                filesInFolder.add(file);
            }
            folderToFileMap.put(folder_, filesInFolder);
        }

        return folderToFileMap;
    }

}
