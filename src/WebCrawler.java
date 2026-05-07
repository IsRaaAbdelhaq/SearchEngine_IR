import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WebCrawler {

    private static final String SEED_URL = "https://en.wikipedia.org/wiki/List_of_pharaohs";
    private static final int MAX_PAGES = 10;
    private static final String OUTPUT_DIR = "crawled_pages";

    // BFS queue and visited set
    private Queue<String> queue = new LinkedList<>();
    private Set<String> visited = new HashSet<>();

    // Stores pageIndex → URL (so other modules can use it)
    public List<String> visitedUrls = new ArrayList<>();

    public void crawl() throws IOException {
        // Create output directory
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        queue.add(SEED_URL);
        int pageCount = 0;

        while (!queue.isEmpty() && pageCount < MAX_PAGES) {
            String url = queue.poll();

            // Skip already visited
            if (visited.contains(url)) continue;
            visited.add(url);

            System.out.println("Crawling [" + pageCount + "]: " + url);

            try {
                // Fetch the page with a user-agent and timeout
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (compatible; InfoRetrievalBot/1.0)")
                        .timeout(5000)
                        .get();

                // Save raw HTML to file
                String filename = OUTPUT_DIR + "/page_" + pageCount + ".html";
                Files.writeString(Paths.get(filename), doc.html());

                visitedUrls.add(url);
                pageCount++;

                // Extract all Wikipedia internal links and enqueue them
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String href = link.attr("abs:href");
                    // Only follow English Wikipedia article links
                    if (href.startsWith("https://en.wikipedia.org/wiki/")
                        && !href.contains("#")
                        && !href.contains("Main_Page")
                        && !href.contains("Special:")
                        && !href.contains("Help:")
                        && !href.contains("File:")
                        && !href.contains("Category:")
                        && !visited.contains(href)) {
                        queue.add(href);
                    }
                }

            } catch (Exception e) {
                System.err.println("Failed to fetch: " + url + " — " + e.getMessage());
            }
        }

        // Save the list of visited URLs to a text file for other members
        saveUrlList();
        System.out.println("\nCrawling complete! " + pageCount + " pages saved to /" + OUTPUT_DIR);
    }

    private void saveUrlList() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < visitedUrls.size(); i++) {
            sb.append("page_").append(i).append(".html").append("\t").append(visitedUrls.get(i)).append("\n");
        }
        Files.writeString(Paths.get(OUTPUT_DIR + "/urls.txt"), sb.toString());
        System.out.println("URL list saved to " + OUTPUT_DIR + "/urls.txt");
    }

    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler();
        crawler.crawl();
    }
}