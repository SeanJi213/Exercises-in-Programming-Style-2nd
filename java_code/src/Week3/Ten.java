package Week3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
public class Ten {

  private final static String DELIMITER = "[^a-zA-Z0-9]+";

  private static Function<Object, Object> readFile = input -> {
    String file = (String) input;
    String dataString = "";
    try {
      dataString = Files.readString(Path.of("src/", file));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dataString;
  };

  private static Function<Object, Object> filterCharsAndNormalize = input -> {
    String dataString = (String) input;
    return dataString.replaceAll(DELIMITER, " ").replaceAll(" s ", " ").toLowerCase();
  };

  private static Function<Object, Object> scan = input -> {
    String dataString = (String) input;
    return Arrays.asList(dataString.split(DELIMITER));
  };

  private static Function<Object, Object> removeStopWords = input -> {
    List<String> words = (List<String>) input;
    List<String> tidyWords = new ArrayList<>();
    try {
      List<String> stopWords = List.of(Files.readString(Path.of("src/stop_words.txt")).split(","));
      for (String word : words) {
        if (!stopWords.contains(word)) {
          tidyWords.add(word);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tidyWords;
  };

  private static Function<Object, Object> frequencies = input -> {
    List<String> words = (List<String>) input;
    Map<String, Integer> pairs = new HashMap<>();
    for (String w : words) {
      pairs.put(w, pairs.getOrDefault(w, 0) + 1);
    }
    return pairs;
  };

  private static Function<Object, Object> sort = input -> {
    Map<String, Integer> wordFrequencies = (Map<String, Integer>) input;
    List<Map.Entry<String, Integer>> sortList = new ArrayList<>(wordFrequencies.entrySet());
    sortList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
    return sortList;
  };

  private static Function<Object, Object> print = input -> {
    List<Map.Entry<String, Integer>> sortList = (List<Map.Entry<String, Integer>>) input;
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%s  -  %s", sortList.get(0).getKey(), sortList.get(0).getValue()));
    for (int i = 1; i < 25 && i < sortList.size(); i++) {
      Map.Entry<String, Integer> item = sortList.get(i);
      sb.append(String.format("\n%s  -  %s", item.getKey(), item.getValue()));
    }
    return sb.toString();
  };

  private static class TFTheOne {
    private Object value;

    private TFTheOne(Object value) {
      this.value = value;
    }

    private TFTheOne bind(Function function) {
      this.value = function.apply(value);
      return this;
    }

    private void print() {
      System.out.println(value);
    }
  }

  public static void main(String[] args) {
      (new TFTheOne("pride-and-prejudice.txt"))
          .bind(readFile)
          .bind(filterCharsAndNormalize)
          .bind(scan)
          .bind(removeStopWords)
          .bind(frequencies)
          .bind(sort)
          .bind(print)
          .print();
  }
}