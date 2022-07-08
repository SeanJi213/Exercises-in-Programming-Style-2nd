package Week5;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class frequencies1 implements Function<List<String>, List<Entry<String, Integer>>> {


  @Override
  public List<Entry<String, Integer>> apply(List<String> words) {
    Map<String, Integer> wfPairs = new HashMap<>();
    //count
    for (String word: words) {
      wfPairs.put(word, wfPairs.getOrDefault(word, 0) + 1);
    }
    // sort
    List<Map.Entry<String, Integer>> list = new ArrayList<>(wfPairs.entrySet());
    list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

    //top25
    List<Map.Entry<String, Integer>> top25List = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      top25List.add(list.get(i));
    }
    return top25List;
  }
}
