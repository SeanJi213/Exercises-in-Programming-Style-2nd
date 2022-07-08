package Week1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class termFrequency {

  public static void main(String[] args) throws IOException {
    File file = new File("src/pride-and-prejudice.txt");
    String tidyWords = filter(file);
    String[] splitWords = lowerCaseAndSplit(tidyWords);
    List<String> wordlist = removeStopWords(splitWords);
    Map<String, Integer> pairs = calculateFrequencies(wordlist);
    List<WordFrequencyPair> sortedPairs = sort(pairs);
    List<String> topPairs = topRank(sortedPairs, 25);
    printTopRank(topPairs);
  }


  public static String filter(File f) throws FileNotFoundException {

    StringBuilder sb = new StringBuilder();
    try (Scanner scanner = new Scanner(f)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        sb.append(line).append(" ");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    String book = sb.toString();
    String words = book.replaceAll("[^a-zA-Z]", " ")
                       .replaceAll(" s ", " ");
    return words.trim().replaceAll(" +", " ");
  }

  public static String[] lowerCaseAndSplit(String str) {
    return str.toLowerCase().split(" ");
  }

  public static List<String> removeStopWords(String[] splitWords) {
    File stopWordFile = new File("src/stop_words.txt");
    StringBuilder sb_stopWord = new StringBuilder();
    try (Scanner scanner = new Scanner(stopWordFile).useDelimiter(",")) {
      while (scanner.hasNext()) {
        String word = scanner.next();
        sb_stopWord.append(word).append(" ");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String stopWords = sb_stopWord.toString();
    String[] splitStopWords = stopWords.split(" ");
    List<String> wordListUpdate = new LinkedList<>(Arrays.asList(splitWords));
    List<String> stopWordList = new LinkedList<>(Arrays.asList(splitStopWords));

    for (String stopWord : stopWordList) {
      wordListUpdate.removeAll(Collections.singleton(stopWord));
    }
    return wordListUpdate;
  }

  public static Map<String, Integer> calculateFrequencies(List<String> wl) {
    Map<String, Integer> pairs = new HashMap<>();
    for (String w : wl) {
      pairs.merge(w, 1, Integer::sum);
    }
    return pairs;
  }

  public static List<WordFrequencyPair> sort(Map<String, Integer> pairs) {
    List<WordFrequencyPair> pairList = new LinkedList<>();
    for (Map.Entry<String, Integer> e : pairs.entrySet()) {
      pairList.add(new WordFrequencyPair(e.getKey(), e.getValue()));
    }

    Collections.sort(pairList);
    Collections.reverse(pairList);
    return pairList;
  }

  public static List<String> topRank(List<WordFrequencyPair> pairList, int rankTo) {
    int count = 0;
    List<String> topRankPairs = new LinkedList<>();
    for (WordFrequencyPair e : pairList) {
      topRankPairs.add(e.getWord() + "  -  " + e.getFrequency());
      count++;
      if (count >= rankTo) {
        break;
      }
    }
    return topRankPairs;
  }

  public static void printTopRank(List<String> list) {
    list.forEach(System.out::println);
  }


  //Used to store words and corresponding frequencies and convenient to sort words by frequency
  private static class WordFrequencyPair implements Comparable<WordFrequencyPair> {

    private String word;
    private int frequency;

    public WordFrequencyPair(String word, int frequency) {
      this.word = word;
      this.frequency = frequency;
    }

    public String getWord() {
      return word;
    }

    public int getFrequency() {
      return frequency;
    }

    @Override
    public int compareTo(WordFrequencyPair other) {
      return this.frequency - other.frequency;
    }
  }

}

/*
* private static Map<String,Integer> sortTheMap(Map<String,Integer> termFreqMap) {
    List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(termFreqMap.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return (o2.getValue()).compareTo(o1.getValue());
        }
    });

    HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }*/