package Week2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Six {


  public static void main(String[] args) throws IOException {
    printTopCount((tidyWords("src/pride-and-prejudice.txt")));
  }

  private static String readFile(String filename) throws IOException {
    Path p = Path.of(filename);
    return Files.readString(p);
  }
  private static List<String> getStopWords() throws IOException {
    return List.of(readFile("src/stop_words.txt").split(","));
  }

  private static List<String> tidyWords(String file) throws IOException {
    List<String> stopWords = getStopWords();
    String book = readFile(file).toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    List<String> words = new LinkedList<>();
    while(matcher.find()){
      if (!stopWords.contains(matcher.group()))
        words.add(matcher.group());
    }
    return words;
  }

  private static void printTopCount(List<String> words){
    Map<String, Integer> tfPairs = new HashMap<>();
    for (String word : words){
      tfPairs.merge(word, 1, Integer::sum);
    }
    Map<String, Integer> reversePair = new HashMap<>();
    tfPairs.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(25).forEachOrdered(pair -> reversePair.put(pair.getKey(), pair.getValue()));
    reversePair.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEach(pair -> System.out.printf("%s  -  %d \n", pair.getKey(), pair.getValue()));
  }
}
