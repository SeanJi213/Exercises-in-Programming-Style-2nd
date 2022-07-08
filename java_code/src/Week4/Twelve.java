package Week4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Twelve {

  private static class dataStorageManager{
    private String book;

    private dataStorageManager(){
      book = "";
    }

    private String init(String filename) throws IOException {
      Path path = Path.of("src/", filename);
      String file = Files.readString(path);
      book = file.toLowerCase();
      return book;
    }

    private List<String> words(){
      Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
      List<String> wordList = new ArrayList<>();
      while (matcher.find())
        wordList.add(matcher.group());
      return wordList;
    }

    private Object dispatch(String[] message) throws IOException {
      if (message[0].equals("init"))
        return this.init(message[1]);
      else if (message[0].equals("words"))
        return this.words();
      else
        throw new IllegalArgumentException("message not found: " + message[0]);
    }
  }


  private static class stopWordManager {
    private List<String> stopWords;

    private stopWordManager(){
      stopWords = new ArrayList<>();
    }

    private List<String> init(String stopWordFile) throws IOException {
      Path path = Path.of("src/", stopWordFile);
      String sw = Files.readString(path);
      stopWords = List.of(sw.split(","));
      return stopWords;
    }

    private boolean isStopWord(String word){
      return stopWords.contains(word);
    }

    private Object dispatch(String[] message) throws IOException {
      if (message[0].equals("init"))
        return this.init(message[1]);
      else if (message[0].equals("isStopWord"))
        return this.isStopWord(message[1]);
      else
        throw new IllegalArgumentException("message not found: " + message[0]);
    }
  }

  private static class wordFrequencyManager{
    private Map<String, Integer> tfMap;

    private wordFrequencyManager(){
      tfMap = new HashMap<>();
    }

    private Map<String, Integer> count(String word){
      if (tfMap.containsKey(word))
        tfMap.put(word, tfMap.get(word) + 1);
      else
        tfMap.put(word, 1);
      return tfMap;
    }

    private List<Map.Entry<String, Integer>> sort(){
      Map<String, Integer> tfSorted = new LinkedHashMap<>();
      tfMap.entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(pair -> tfSorted.put(pair.getKey(), pair.getValue()));
      List<Map.Entry<String, Integer>> list = new ArrayList<>();
      for (Map.Entry<String, Integer> pair : tfSorted.entrySet())
        list.add(pair);
      return list;
    }

    private Object dispatch(String[] message){
      if (message[0].equals("count"))
        return this.count(message[1]);
      else if (message[0].equals("sort"))
        return this.sort();
      else
        throw new IllegalArgumentException("message not found: " + message[0]);
    }
  }

  private static class wordFrequencyController{
    dataStorageManager dataManager;
    stopWordManager stopWordManager;
    wordFrequencyManager tfManager;

    private wordFrequencyController(){};

    private void init(String filename) throws IOException {
      dataManager = new dataStorageManager();
      stopWordManager = new stopWordManager();
      tfManager = new wordFrequencyManager();
      dataManager.dispatch(new String[]{"init", filename});
      stopWordManager.dispatch(new String[]{"init", "stop_words.txt"});
    }

    @SuppressWarnings("unchecked")
    private void run() throws IOException {
      for(String word : (List<String>) dataManager.dispatch(new String[]{"words"})){
        if (!(boolean) stopWordManager.dispatch(new String[]{"isStopWord", word}))
          tfManager.dispatch(new String[]{"count", word});
      }

      List<Map.Entry<String, Integer>> pairs = (List<Map.Entry<String, Integer>>) tfManager.dispatch(new String[]{"sort"});
      for (int i = 0; i < 25; i++) {
        System.out.println(pairs.get(i).getKey() + "  -  " + pairs.get(i).getValue());
      }
    }

    private void dispatch(String[] message) throws IOException {
      if (message[0].equals("init"))
       this.init(message[1]);
      else if (message[0].equals("run"))
        this.run();
      else
        throw new IllegalArgumentException("message not found: " + message[0]);
    }
  }

  public static void main(String[] args) throws IOException {
    wordFrequencyController controller = new wordFrequencyController();
    controller.dispatch(new String[]{"init", "pride-and-prejudice.txt"});
    controller.dispatch(new String[]{"run"});
  }

}
