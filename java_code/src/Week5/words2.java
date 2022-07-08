package Week5;

import java.util.*;
import java.util.function.*;
import java.nio.file.*;

class words2 implements Function<String, String[]>{

  @Override
  public String[] apply(String file) {

    List<String> stopWords = new ArrayList<>();
    try {
      stopWords = new ArrayList<>(
          Arrays.asList(new String(Files.readAllBytes(Paths.get("src/stop_words.txt"))).split(",")));
    } catch (Exception e) {
      System.out.println(e);
    }

    List<String> words = new ArrayList<>();
    try {
      words = Arrays.asList(new String(Files.readAllBytes(Paths.get(file)))
          .replaceAll("\\P{Alnum}", " ").toLowerCase().split(","));
    } catch (Exception e) {
      System.out.println(e);
    }
    List<String> resWords = new ArrayList<>();
    for (String word : words) {
      if (word.length() >= 2) {
        resWords.add(word);
      }
    }
    resWords.removeAll(stopWords);
    return resWords.toArray(new String[resWords.size()]);
  }
}