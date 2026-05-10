package src;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        // =====================================
        // STEP 1: WEB CRAWLING
        // =====================================

        System.out.println("Starting Web Crawler...\n");

        WebCrawler crawler = new WebCrawler();

        crawler.crawl();

        // =====================================
        // STEP 2: HTML PARSING
        // =====================================

        System.out.println("\nStarting HTML Parsing...\n");

        HtmlParser parser = new HtmlParser();

        parser.parseAndClean();

        // =====================================
        // STEP 3: BUILD INVERTED INDEX
        // =====================================

        System.out.println("\nBuilding Inverted Index...\n");

        InvertedIndex index = new InvertedIndex();

        index.buildIndex();
  

        // =====================================
        // STEP 4: DOCUMENT COUNT
        // =====================================

        File folder = new File("clean_pages");

        int totalDocs =
                folder.listFiles(
                        (dir, name) -> name.endsWith(".txt")
                ).length;

        System.out.println("Total Documents = " + totalDocs);

        // =====================================
        // STEP 5: TF-IDF
        // =====================================

        System.out.println("\nComputing TF-IDF...\n");

        TFIDFVectorizer vectorizer =
                new TFIDFVectorizer(
                        index.index,
                        totalDocs
                );

        vectorizer.computeIDF();

        vectorizer.buildDocumentVectors();

        System.out.println("TF-IDF vectors created successfully!");

        // =====================================
        // STEP 6: QUERY INPUT
        // =====================================

        Scanner input = new Scanner(System.in);

        System.out.println("\nEnter Search Query:");

        String query = input.nextLine();

        // =====================================
        // STEP 7: QUERY VECTOR
        // =====================================

        Map<String, Double> queryVector =
                vectorizer.buildQueryVector(query);

        // =====================================
        // STEP 8: COSINE SIMILARITY
        // =====================================

        System.out.println("\nComputing Similarities...\n");

        Map<String, Double> scores =
                vectorizer.computeCosineSimilarity(queryVector);

        // =====================================
        // STEP 9: NORMALIZATION
        // =====================================

        vectorizer.normalizeScores(scores);

        // =====================================
        // STEP 10: RANKING
        // =====================================

        Ranker ranker = new Ranker();

        ranker.rankDocuments(scores, 10);

        System.out.println("\nSearch Complete!");
    }
}