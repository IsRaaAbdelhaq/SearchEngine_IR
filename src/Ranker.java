package src;
import java.util.*;

public class Ranker {

    public void rankDocuments(
            Map<String, Double> scores,
            int k
    ) {

        List<Map.Entry<String, Double>> ranked =
                new ArrayList<>(scores.entrySet());

        ranked.sort((a, b) ->
                Double.compare(
                        b.getValue(),
                        a.getValue()
                )
        );

        System.out.println("\n========== TOP RESULTS ==========\n");

        for (int i = 0;
             i < Math.min(k, ranked.size());
             i++) {

            System.out.println(
                    (i + 1)
                            + "- "
                            + ranked.get(i).getKey()
                            + " | Similarity Score = "
                            + ranked.get(i).getValue()
            );
        }
    }
}