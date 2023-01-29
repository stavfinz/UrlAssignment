package voyagerlabs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DepthIterations {

    private String initialUrlString;

    private int maxDifferentURLs;

    private int depthFactor;

    private boolean uniqueness;

    private List<String> allUrlsFound;

    public DepthIterations(String urlString, int maxDifferentURLs, int depthFactor, boolean uniqueness) {
        this.initialUrlString = urlString;
        this.maxDifferentURLs = maxDifferentURLs;
        this.depthFactor = depthFactor;
        this.uniqueness = uniqueness;
        this.allUrlsFound = new ArrayList<String>(maxDifferentURLs ^ depthFactor);
    }

    public void doIterations() {
        Map<Integer, ArrayList<String>> depthMap = new HashMap<>();
        ArrayList<String> initialList = new ArrayList<>();
        initialList.add(initialUrlString);
        depthMap.put(0, initialList); // depth 0 -> the URL input we got
        allUrlsFound.add(initialUrlString);

        for (int depth = 0; depth <= this.depthFactor; depth++) {
            List<String> urlExtracted = depthMap.get(depth);
            for (int i = 0; i < urlExtracted.size(); i++) {
                String currentUrlString = urlExtracted.get(i);
                try {
                    Document doc = Jsoup.connect(currentUrlString).get();
                    String filePathString = depth + "/" + currentUrlString.replaceAll("[^a-zA-Z0-9]", "_");
                    final File f = new File(filePathString + ".html");
                    FileUtils.writeStringToFile(f, doc.outerHtml(), StandardCharsets.UTF_8);

                    if (depth != this.depthFactor) {
                        ArrayList<String> extractNewUrls = extractNewUrls(doc);
                        depthMap.computeIfAbsent(depth + 1, d -> new ArrayList<String>(Arrays.asList()))
                                .addAll(extractNewUrls);
                    }

                } catch (IOException e) {
                    System.out.println("Something went wrong -> Connection / Save To file  ---> FAILED ---> Exception: " + e);

                }
            }
        }
    }

    public ArrayList<String> extractNewUrls(Document doc) {

        Elements linkElements = doc.select("a[href]"); // a with href
        ArrayList<String> urlFoundList = new ArrayList<>(this.maxDifferentURLs);
        for (Element linkElement : linkElements) {
            if (urlFoundList.size() < this.maxDifferentURLs) {
                String link = linkElement.attr("abs:href");
                if (!isValidAndReachableUrl(link)) {
                    continue;
                }
                if (this.uniqueness) {
                    if (!this.allUrlsFound.contains(link)) {
                        allUrlsFound.add(link);
                        urlFoundList.add(link);
                    } else {
                        continue;
                    }
                } else {
                    allUrlsFound.add(link);
                    urlFoundList.add(link);
                }
            } else {
                break;
            }
        }
        return urlFoundList;
    }

    public boolean isValidAndReachableUrl(String link) {
        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            // the URL is not in a valid form
            return false;
        } catch (IOException e) {
            // the connection couldn't be established
            return false;
        }
    }

}
