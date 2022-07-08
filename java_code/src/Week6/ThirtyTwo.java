package Week6;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;


public class ThirtyTwo {

  static String data;

  static class tfPair<K, V> {
    public K key;
    public V value;

    public tfPair(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  public static void readFile(String path) {
    StringBuilder sb = null;
    try {
      BufferedReader buffReader = new BufferedReader(new FileReader(path));
      sb = new StringBuilder();
      String tmp;
      while((tmp = buffReader.readLine()) != null){
        sb.append(tmp);
        sb.append("\n");
      }
      buffReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    data = sb.toString();
  }

  public static List<String[]> partition(int n) {
    List<String[]> part = new ArrayList<>();
    String[] list = data.split("\n");

    for (int i = 0; i < list.length; i = i + n) {
      part.add(Arrays.copyOfRange(list, i, Math.min(i + n, list.length)));
    }
    return part;
  }

  static class WordProcessWorker {
    List<String> stopWords;
    List<tfPair<String, Integer>> list;

    public WordProcessWorker() {
      stopWords = new ArrayList<>();
      list = new ArrayList<>();
    }

    public List<tfPair<String, Integer>> getList() {
      return list;
    }

    public void removeStopWords(String[] wordList) {
      String path = "../stop_words.txt";
      StringBuilder sb = null;
      try {
        BufferedReader buffReader = new BufferedReader(new FileReader(path));
        sb = new StringBuilder();
        String tmp;
        while((tmp = buffReader.readLine()) != null){
          String[] arr = tmp.split(",");
          stopWords.addAll(Arrays.asList(arr));
        }
        buffReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      for (String str : wordList) {
        String[] words = str.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
        for (String word : words)
          if (!stopWords.contains(word) && word.length() > 1)
            list.add(new tfPair(word, 1));
      }
    }
  }


  public static void map(WordProcessWorker wordProcessWorker, List<String[]> list) {
    for (String[] wordList : list) {
      wordProcessWorker.removeStopWords(wordList);
    }
  }

  static class Reducer extends Thread {
    List<tfPair<String, Integer>> list;
    Map<String, Integer> map;

    public Reducer() {
      list = new ArrayList<>();
      map = new HashMap<>();
    }

    @Override
    public void run() {
      for (tfPair<String, Integer> p : list) {
        String word = p.key;
        map.put(word, map.getOrDefault(word, 0) + 1);
      }
    }
  }

  public static void regroup(List<tfPair<String, Integer>> list, Reducer[] reducer) {
    for (tfPair<String, Integer> pair : list) {
      char ch = pair.key.charAt(0);
      if (ch >= 'a' && ch <= 'e') {
        reducer[0].list.add(pair);
      } else if (ch >= 'f' && ch <= 'j') {
        reducer[1].list.add(pair);
      } else if (ch >= 'k' && ch <= 'o') {
        reducer[2].list.add(pair);
      } else if (ch >= 'p' && ch <= 't') {
        reducer[3].list.add(pair);
      } else if (ch >= 'u' && ch <= 'z') {
        reducer[4].list.add(pair);
      }
    }
  }

  public static void main(String[] args) throws Exception{
    readFile("../pride-and-prejudice.txt");
    WordProcessWorker wordProcessWorker = new WordProcessWorker();
    map(wordProcessWorker, partition(100));

    Reducer[] reducers = new Reducer[5];
    for (int i = 0; i < 5; i++) {
      reducers[i] = new Reducer();
    }

    regroup(wordProcessWorker.getList(), reducers);
    for (Thread t : reducers) {
      t.start();
    }
    for (Thread t : reducers) {
      t.join();
    }

    Map<String, Integer> counter = new HashMap<>();
    for (Reducer reducer : reducers) {
      for (Map.Entry<String, Integer> entry : reducer.map.entrySet()) {
        counter.put(entry.getKey(), counter.getOrDefault(entry.getKey(), 0) + entry.getValue());
      }
    }

    List<Map.Entry<String, Integer>> list = new ArrayList<>(counter.entrySet());
    list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

    // top25
    for (int i = 0; i < 25; i++) {
      System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
    }
  }
}