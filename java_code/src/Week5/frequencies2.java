package Week5;

import java.util.*;
import java.util.function.*;

// PriorityQueue
class frequencies2 implements Function<String[], List<Map.Entry<String, Integer>>> {

  @Override
  public List<Map.Entry<String, Integer>> apply(String[] words) {
    Map<String, Integer> tfPairs = new HashMap<>();
    for (String word: words) {
      tfPairs.put(word, tfPairs.getOrDefault(word, 0) + 1);
    }

    // sort
    PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((o1, o2) -> (o2.getValue() - o1.getValue()));

    for (Map.Entry<String, Integer> entry : tfPairs.entrySet()) {
      pq.offer(entry);
    }

    List<Map.Entry<String, Integer>> resList = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      resList.add(pq.poll());
    }
    return resList;
  }
}