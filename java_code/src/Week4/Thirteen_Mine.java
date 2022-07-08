package Week4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Thirteen_Mine {

  private static Map<String, Object> dataStorageObj = new HashMap<>();
  private static Map<String, Object> stopWordObj = new HashMap<>();
  private static Map<String, Object> termFrequencyObj = new HashMap<>();


  private static void loadBook(String filename, Map<String, Object> dataStorageMap) throws IOException {
    Path p = Path.of("src/" + filename);
    String book = Files.readString(p).toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    List<String> wordList = new ArrayList<>();
    while (matcher.find())
      wordList.add(matcher.group());
    dataStorageMap.put("init", wordList);
  }


  private static void loadStopWords(Map<String, Object> stopWordMap) throws IOException{
    Path p = Path.of("src/stop_words.txt");
    List<String> stopWords = List.of(Files.readString(p).split(","));
    stopWordMap.put("init", stopWords);
  }

  private static List<Map.Entry<String, Integer>> sort(Map<String, Object> tfObjectMap) {
    Map<String, Integer> tfSorted = new LinkedHashMap<>();
    Map<String, Integer> pairs = (Map<String, Integer>)tfObjectMap.get("freqs");
    pairs.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEachOrdered(pair -> tfSorted.put(pair.getKey(), pair.getValue()));
    List<Map.Entry<String, Integer>> list = new ArrayList<>();
    for (Map.Entry<String, Integer> pair : tfSorted.entrySet())
      list.add(pair);
    return list;
  }

  private static void top25(Map<String, Object> tfObjMap){
    List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)tfObjMap.get("top25");
    for (int i = 0; i < 25; i++) {
      System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
    }
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    //add "things" into the stopWordObj map
    stopWordObj.put("data", new ArrayList<String>());
    stopWordObj.put("init", (Consumer) (x) -> {
      try {
        loadStopWords(stopWordObj);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    stopWordObj.put("check", (Predicate)((word) -> ((ArrayList<String>)stopWordObj.get("init")).contains((String)word)));

    //add "things" into the dataStorageObj map
    dataStorageObj.put("data", new ArrayList<String>());
    dataStorageObj.put("init", (Consumer)(filename -> {
      try {
        loadBook((String)filename, dataStorageObj);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));

    //add "things" into the term frequency object map
    termFrequencyObj.put("freqs", new LinkedHashMap<String, Integer>());
    termFrequencyObj.put("count", (Consumer)((word) ->  ((HashMap<String, Integer>)termFrequencyObj.get("freqs"))
        .put((String)word, ((HashMap<String, Integer>)termFrequencyObj.get("freqs")).getOrDefault(word, 0) + 1)));
    termFrequencyObj.put("sort", (Supplier) () -> sort(termFrequencyObj));
    termFrequencyObj.put("top25", (Consumer) (x) -> top25(termFrequencyObj));


    ((Consumer)dataStorageObj.get("init")).accept("pride-and-prejudice.txt");
    ((Consumer)stopWordObj.get("init")).accept(0);
    for (String word : (ArrayList<String>)((Supplier)dataStorageObj.get("init")).get()){
      if (!((Predicate)stopWordObj.get("check")).test(word))
        ((Consumer)termFrequencyObj.get("count")).accept(word);
    }

    ((Supplier)termFrequencyObj.get("sort")).get();
    ((Consumer)termFrequencyObj.get("top25")).accept(0);
  }
}
