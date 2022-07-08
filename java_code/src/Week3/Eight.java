package Week3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Eight {


  public static void main(String[] args) throws IOException {
    mergeFrequency(tidyWords("src/pride-and-prejudice.txt"));
    bubbleSort(entryList.size());
    printTopCount();
  }

  private static List<Entry<String, Integer>> entryList = new ArrayList<>();

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

  private static void mergeFrequency(List<String> wl){
    Map<String, Integer> tfPairs = new LinkedHashMap<>();
    for (String word : wl){
      tfPairs.merge(word, 1, Integer::sum);
    }
    entryList = new ArrayList<Entry<String, Integer>>(tfPairs.entrySet());
  }

  private static void bubbleSort(int size){
    if (size == 1)
      return;

    for (int i = 0; i < size - 1; i++){
      if (entryList.get(i).getValue().compareTo(entryList.get(i + 1).getValue()) < 0){
        Entry<String, Integer> temp = entryList.get(i);
        entryList.set(i, entryList.get(i + 1));
        entryList.set(i + 1, temp);
      }
    }
    bubbleSort(size - 1);
  }

  private static void printTopCount(){
    for (int i = 0; i < 25; i++) {
      System.out.println(entryList.get(i).getKey() + " -  " + entryList.get(i).getValue());
    }
  }

}
