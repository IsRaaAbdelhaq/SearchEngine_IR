import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlParser {

    private static final String INPUT_DIR = "crawled_pages";
    private static final String OUTPUT_DIR = "clean_pages";

    public void parseAndClean() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        File inputDir = new File(INPUT_DIR);
        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".html"));

        if (files == null || files.length == 0) {
            System.out.println("No HTML files found in " + INPUT_DIR + "! Run WebCrawler first.");
            return;
        }

        for (File file : files) {
            Document doc = Jsoup.parse(file, "UTF-8");

            String plainText = doc.text();

            String cleanText = plainText.toLowerCase().replaceAll("[^a-z0-9\\s]", "");

            String outputFileName = OUTPUT_DIR + "/" + file.getName().replace(".html", ".txt");
            Files.writeString(Paths.get(outputFileName), cleanText);

            System.out.println("Cleaned and saved: " + outputFileName);
        }

        System.out.println("\nText extraction complete! Clean files saved to /" + OUTPUT_DIR);
    }

    public static void main(String[] args) throws IOException {
        HtmlParser parser = new HtmlParser();
        parser.parseAndClean();
    }
}