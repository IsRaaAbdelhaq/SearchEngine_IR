import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InvertedIndex {

    private static final String INPUT_DIR = "clean_pages";

    public Map<String, Map<String, Integer>> index = new HashMap<>();

    public void buildIndex() throws IOException {
        File inputDir = new File(INPUT_DIR);
        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No clean text files found in " + INPUT_DIR + "! Run HtmlParser first.");
            return;
        }

        for (File file : files) {

            String docId = file.getName().replace(".txt", "");

            String content = Files.readString(file.toPath());
            String[] words = content.split("\\s+");

            for (String word : words) {
                if (word.isEmpty())
                    continue;

                index.putIfAbsent(word, new HashMap<>());

                Map<String, Integer> postingList = index.get(word);
                postingList.put(docId, postingList.getOrDefault(docId, 0) + 1);
            }
            System.out.println("Indexed document: " + docId);
        }

        System.out.println("\nInverted Index built successfully! Total unique words: " + index.size());
    }

    public void testWord(String word) {
        System.out.println("\n--- Testing Inverted Index for '" + word + "' ---");
        if (index.containsKey(word)) {
            Map<String, Integer> postings = index.get(word);
            for (Map.Entry<String, Integer> entry : postings.entrySet()) {
                System.out.println("Document: " + entry.getKey() + " | TF (Count): " + entry.getValue());
            }
        } else {
            System.out.println("Word '" + word + "' not found in the index.");
        }
    }

    public static void main(String[] args) throws IOException {
        InvertedIndex indexBuilder = new InvertedIndex();
        indexBuilder.buildIndex();
        indexBuilder.testWord("egypt");
    }
}