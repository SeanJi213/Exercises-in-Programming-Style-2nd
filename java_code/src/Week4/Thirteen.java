package Week4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Thirteen {

  private static void loadBook(String filename, Map<String, Object> map) throws IOException {
    Path path = Path.of("src/", filename);
    String book = Files.readString(path).toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    List<String> wordList = new ArrayList<>();
    while (matcher.find())
      wordList.add(matcher.group());

    map.put("data", wordList);
  }


  private static void loadStopWords(Map<String, Object> map) throws IOException {
    List<String> stopWords;
    Path path = Path.of("src/stop_words.txt");
    String sw = Files.readString(path);
    stopWords = new ArrayList<>(Arrays.asList(sw.split(",")));

    map.put("stop_words", stopWords);
  }


  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws IOException{

    Map<String, Object> dataStorageObj = new HashMap<>();
    Map<String, Object> stopWordsObj = new HashMap<>();
    Map<String, Object> termFrequencyObj = new HashMap<>();


    dataStorageObj.put("data", new ArrayList<String>());
    dataStorageObj.put("init", (Consumer) (filename -> {
      try {
        loadBook((String) filename, dataStorageObj);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));
    dataStorageObj.put("words", (Supplier) (() -> dataStorageObj.get("data")));


    stopWordsObj.put("stop_words", new ArrayList<String>());
    stopWordsObj.put("init", (Consumer) ((x) -> {
      try {
        loadStopWords(stopWordsObj);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));
    Predicate<String> check = (Predicate)((word) -> ((ArrayList<String>)stopWordsObj.get("stop_words")).contains((String)word));
    stopWordsObj.put("check", check);


    termFrequencyObj.put("freqs", new HashMap<String, Integer>());
    Consumer<String> count =
        (word) -> ((HashMap<String, Integer>) termFrequencyObj.get("freqs"))
        .put(word, ((HashMap<String, Integer>) termFrequencyObj
        .get("freqs")).getOrDefault(word, 0) + 1);

    Supplier<List<Map.Entry<String, Integer>>> sorted = () -> {
      List<Map.Entry<String, Integer>> list = new ArrayList<>(
          ((HashMap<String, Integer>) termFrequencyObj.get("freqs")).entrySet());
      Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      return list;
    };

    Consumer top25 = (x) -> {
      List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>) ((Supplier) termFrequencyObj.get("sort")).get();
      for (int i = 0; i < 25; i++) {
        System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
      }
    };

    termFrequencyObj.put("count", count);
    termFrequencyObj.put("sort", sorted);
    termFrequencyObj.put("top25", top25);


    ((Consumer) dataStorageObj.get("init")).accept("pride-and-prejudice.txt");
    ((Consumer) stopWordsObj.get("init")).accept(0);
    for (String word : (ArrayList<String>) ((Supplier) dataStorageObj.get("words")).get()) {
      if (!((Predicate) stopWordsObj.get("check")).test(word)) {
        ((Consumer) termFrequencyObj.get("count")).accept(word);
      }
    }
    ((Supplier) termFrequencyObj.get("sort")).get();
    ((Consumer) termFrequencyObj.get("top25")).accept(0);
  }
}