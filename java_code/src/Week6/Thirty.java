package Week6;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;


public class Thirty {
  // word space
  static LinkedBlockingQueue<String> wordSpace;
  // frequency space
  static LinkedBlockingQueue<Map<String, Integer>> freqSpace;

  static List<String> stopWords;

  // word processing workers
  static class WordProcessWorker extends Thread {

    Map<String, Integer> tfPairs;

    public WordProcessWorker() {
      tfPairs = new HashMap<>();
    }

    @Override
    public void run() {
      while (true) {
        String word = null;
        try {
          word = wordSpace.poll(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
          e.printStackTrace();
        }
        if (word == null) {
          break;
        }
        if (!stopWords.contains(word) && word.length() > 1) {
          tfPairs.put(word, tfPairs.getOrDefault(word, 0) + 1);
        }
      }
      freqSpace.offer(tfPairs);
    }
  }

  public static void main(String[] args) throws Exception {

    // initiate word space
    wordSpace = new LinkedBlockingQueue<>();
    // initiate frequency space
    freqSpace = new LinkedBlockingQueue<>();

    // read stop words
    stopWords = new ArrayList<>();
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

    // read file
    sb = null;
    try {
      BufferedReader buffReader = new BufferedReader(new FileReader("../pride-and-prejudice.txt"));
      sb = new StringBuilder();
      String tmp;
      while((tmp = buffReader.readLine()) != null){
        String[] words = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
        for (String word: words) {
          wordSpace.offer(word);
        }
      }
      buffReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // initiate 5 workers
    Thread[] workers = new Thread[5];
    for (int i = 0; i < 5; i++)
      workers[i] = new WordProcessWorker();
    for (Thread t : workers)
      t.start();
    for (Thread t : workers) {
      try {
        t.join();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    Map<String, Integer> counter = new HashMap<>();
    for (Map<String, Integer> map : freqSpace) {
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
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



