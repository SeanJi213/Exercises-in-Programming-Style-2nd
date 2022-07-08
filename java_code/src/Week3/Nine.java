package Week3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class Nine {

  private final static String DELIMITER = "[^a-zA-Z0-9]+";

  private static Consumer<List<Map.Entry<String, Integer>>> print = sortList -> {
    for (int i = 0; i < 25 && i < sortList.size(); i++) {
      Map.Entry<String, Integer> item = sortList.get(i);
      System.out.printf("%s  -  %s%n", item.getKey(), item.getValue());
    }
  };

  private static BiConsumer<Map<String, Integer>, Consumer> sort = (wordFrequencies, function) -> {
    List<Map.Entry<String, Integer>> sortList = new ArrayList<>(wordFrequencies.entrySet());
    sortList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
    function.accept(sortList);
  };

  private static BiConsumer<List<String>, BiConsumer> frequencies = (words, function) -> {
    Map<String, Integer> pairs = new HashMap<>();
    for (String w : words) {
      pairs.put(w, pairs.getOrDefault(w, 0) + 1);
    }
    function.accept(pairs, print);
  };

  private static BiConsumer<List<String>, BiConsumer> removeStopWords = (words, function) -> {
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
    function.accept(tidyWords, sort);
  };

  private static BiConsumer<String, BiConsumer> scan = (dataString, function) -> {
    function.accept(Arrays.asList(dataString.split(DELIMITER)), frequencies);
  };

  private static BiConsumer<String, BiConsumer> filterCharsAndNormalize = (dataString, function) -> {
    function.accept(dataString.replaceAll(DELIMITER, " ").replaceAll(" s ", " ").toLowerCase(), removeStopWords);
  };

  private static BiConsumer<String, BiConsumer> readFile = (file, function) -> {
    String dataString = "";
    try {
      dataString = Files.readString(Path.of("src/", file));
    } catch (IOException e) {
      e.printStackTrace();
    }
    function.accept(dataString, scan);
  };

  public static void main(String[] args) {
      readFile.accept("pride-and-prejudice.txt", filterCharsAndNormalize);
  }

}