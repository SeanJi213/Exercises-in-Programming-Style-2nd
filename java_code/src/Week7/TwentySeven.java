package Week7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class TwentySeven {
  public static void main(String[] args) throws IOException {

    Pair<List<String>, Object> allWords = new Pair<>();
    allWords.setKey(new ArrayList<>());

    Pair<List<String>, Object> stopWords = new Pair<>();
    stopWords.setKey(new ArrayList<>());

    Pair<List<String>, Object> nonStopWords = new Pair<>();
    nonStopWords.setKey(new ArrayList<>());
    nonStopWords.setValue((Supplier)() -> {
      ArrayList<String> nonStopWordsList = new ArrayList<>();
      for (String str : allWords.key) {
        if (!stopWords.key.contains(str)) {
          nonStopWordsList.add(str);
        }
      }
      return nonStopWordsList;
    });

    Pair<List<String>, Object> uniqueWords = new Pair<>();
    uniqueWords.setKey(new ArrayList<>());
    uniqueWords.setValue((Supplier)() -> {
      ArrayList<String> uniqueWordsList = new ArrayList<>(new HashSet<String>(nonStopWords.key));
      return uniqueWordsList;
    });

    Pair<List<Integer>, Object> counts = new Pair<>();
    counts.setKey(new ArrayList<>());
    counts.setValue((Supplier)() -> {
      ArrayList<Integer> countsList = new ArrayList<>();
      for (int i = 0; i < uniqueWords.key.size(); i++) {
        countsList.add(0);
      }

      for (int i = 0; i < nonStopWords.key.size(); i++) {
        int index = uniqueWords.key.indexOf(nonStopWords.key.get(i));
        countsList.set(index, countsList.get(index) + 1);
      }
      return countsList;
    });

    Pair<List<Pair<String, Integer>>, Object> sorted = new Pair<>();
    sorted.setKey(new ArrayList<>());
    sorted.setValue((Supplier)() -> {
      ArrayList<Pair<String, Integer>> sortedList = new ArrayList<>();
      PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>((o1, o2) -> o2.value - o1.value);
      for (int i = 0; i < uniqueWords.key.size(); i++) {
        Pair<String, Integer> pair = new Pair<>(uniqueWords.key.get(i), counts.key.get(i));
        pq.offer(pair);
      }

      while (!pq.isEmpty()) {
        sortedList.add(pq.poll());
      }
      return sortedList;
    });

    // all columns
    ArrayList<Pair> allColumns = new ArrayList<>();
    allColumns.add(allWords);
    allColumns.add(stopWords);
    allColumns.add(nonStopWords);
    allColumns.add(uniqueWords);
    allColumns.add(counts);
    allColumns.add(sorted);

    Path bookPath = Path.of("../", "pride-and-prejudice.txt");
    String book = Files.readString(bookPath).toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    while (matcher.find())
      allWords.key.add(matcher.group());

    Path stopWordPath = Path.of("../stop_words.txt");
    stopWords.key.addAll(List.of(Files.readString(stopWordPath).split(",")));

    // update
    for (Pair cur : allColumns) {
      if (cur.value != null) {
        cur.key = ((Supplier)cur.value).get();
      }
    }

    // print top25
    for (int i = 0; i < 25; i++) {
      System.out.println(sorted.key.get(i).getKey() + "  -  " + sorted.key.get(i).getValue());
    }

    // make it interactive
    while (true) {
      System.out.println("");
      System.out.println("Input the name of the file you want to add or input 'end' to exit:");
      Scanner sc = new Scanner(System.in);
      String file = sc.next();
      if (file.equals("end")) {
        break;
      }

      Path p = Path.of("../", file);
      String f = Files.readString(bookPath).toLowerCase();
      Matcher m = Pattern.compile("[a-z]{2,}").matcher(f);
      while (m.find())
        allWords.key.add(m.group());

      for (Pair cur : allColumns) {
        if (cur.value != null) {
          cur.key = ((Supplier)cur.value).get();
        }
      }

      for (int i = 0; i < 25; i++) {
        System.out.println(sorted.key.get(i).getKey() + "  -  " + sorted.key.get(i).getValue());
      }
    }
  }
}

class Pair<K, V> {
  public K key;
  public V value;

  public Pair() {
    this.key = null;
    this.value = null;
  }

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return this.key;
  }

  public V getValue() {
    return this.value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public void setValue(V value) {
    this.value = value;
  }
}

