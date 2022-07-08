package Week2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Five {

  public static List<String> stopWords = new LinkedList<>();
  public static String book;
  public static List<String> words = new LinkedList<>();
  public static Map<String, Integer> tfTop = new HashMap<>();

  public static void main(String[] args) throws IOException {
    getStopWords();
    tidyWords(args[0]);
    printTopCount();
  }

  private static void getStopWords() throws IOException {
    Path p = Path.of("src/stop_words.txt");
    stopWords = List.of(Files.readString(p).split(","));
  }

  private static void tidyWords(String str) throws IOException {
    Path p = Path.of("src/", str);
    String book = Files.readString(p).toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    while(matcher.find()){
      if (!stopWords.contains(matcher.group()))
        words.add(matcher.group());
    }
  }

  private static void printTopCount(){
    Map<String, Integer> tfPairs = new HashMap<>();
    for (String word : words){
      tfPairs.merge(word, 1, Integer::sum);
    }
    tfPairs.entrySet().stream()
           .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
           .limit(25).forEachOrdered(pair -> tfTop.put(pair.getKey(), pair.getValue()));

    tfTop.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEach(pair -> System.out.printf("%s  -  %d \n", pair.getKey(), pair.getValue()));
  }

}
