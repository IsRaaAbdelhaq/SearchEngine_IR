import java.util.*;


public class TFIDFVectorizer {

    private int totalDocs;
    private Map<String, Map<String, Integer>> invertedIndex;
    private Map<String, Double> idf;
    private Map<String, Map<String, Double>> docVectors;

    public TFIDFVectorizer(Map<String, Map<String, Integer>> invertedIndex, int totalDocs) {
        this.invertedIndex = invertedIndex;
        this.totalDocs     = totalDocs;
        this.idf           = new HashMap<>();
        this.docVectors    = new HashMap<>();
    }

    public void computeIDF() {
        for (String term : invertedIndex.keySet()) {
            int df = invertedIndex.get(term).size();
            double idfValue = Math.log((double) totalDocs / df);
            idf.put(term, idfValue);
        }
    }

    public void buildDocumentVectors() {
        for (String term : invertedIndex.keySet()) {
            double idfValue = idf.getOrDefault(term, 0.0);

            for (Map.Entry<String, Integer> entry : invertedIndex.get(term).entrySet()) {
                String docId  = entry.getKey();
                int    tf     = entry.getValue();
                double weight = tf * idfValue;

                docVectors.computeIfAbsent(docId, k -> new HashMap<>())
                          .put(term, weight);
            }
        }
    }

  
    public Map<String, Double> buildQueryVector(String query) {
        Map<String, Double> queryVector = new HashMap<>();

        String[] tokens = query.toLowerCase().trim().split("\\s+");

        Map<String, Integer> queryTF = new HashMap<>();
        for (String token : tokens) {
            queryTF.put(token, queryTF.getOrDefault(token, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : queryTF.entrySet()) {
            String term = entry.getKey();
            int    tf   = entry.getValue();
            if (idf.containsKey(term)) {
                queryVector.put(term, tf * idf.get(term));
            }
        }

        return queryVector;
    }

    public Map<String, Map<String, Double>> getDocVectors() { return docVectors; }
    public Map<String, Double> getIDF()                     { return idf; }
}
