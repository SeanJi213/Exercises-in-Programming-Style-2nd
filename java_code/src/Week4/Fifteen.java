package Week4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fifteen {

  public static class wordFrequencyFramework{
    private List<Consumer<String>> loadEventHandlers;
    private List<Runnable> doWorkEventHandlers;
    private List<Runnable> endEventHandlers;

    public wordFrequencyFramework() {
      loadEventHandlers = new LinkedList<>();
      doWorkEventHandlers = new LinkedList<>();
      endEventHandlers = new LinkedList<>();
    }

    public void registerForLoadEvent(Consumer<String> handler){
      loadEventHandlers.add(handler);
    }

    public void registerForDoWorkEvent(Runnable handler){
      doWorkEventHandlers.add(handler);
    }

    public void registerForEndEvent(Runnable handler){
      endEventHandlers.add(handler);
    }

    public void run(String filename){
      for (Consumer h : loadEventHandlers)
        h.accept(filename);
      for (Runnable h : doWorkEventHandlers)
        h.run();
      for (Runnable h : endEventHandlers)
        h.run();
    }

  }

  public static class dataStorage {

    private List<String> data;
    private stopWordFilter swFilter;
    private List<Consumer> wordEventHandlers;

    public dataStorage(wordFrequencyFramework wfapp, stopWordFilter swFilter) {
      this.data = new ArrayList<>();
      this.swFilter = swFilter;
      this.wordEventHandlers = new LinkedList<>();
      wfapp.registerForLoadEvent(this::load);
      wfapp.registerForDoWorkEvent(this::produceWords);
    }

    public void load(String f) {
      try {
        Path p = Path.of("src/", f);
        String book = Files.readString(p).toLowerCase();
        Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
        while (matcher.find())
          this.data.add(matcher.group());
      } catch (IOException ie) {
        ie.printStackTrace();
      }
    }

    public void produceWords(){
      for (String str : this.data) {
        if (!this.swFilter.isStopWord(str)) {
          for (Consumer<String> h : wordEventHandlers)
            h.accept(str);
        }
      }
    }

    private void registerForWordEvent(Consumer<String> handler){
      wordEventHandlers.add(handler);
    }
  }

  public static class stopWordFilter{
    private List<String> stopWords;

    public stopWordFilter(wordFrequencyFramework wfapp){
      stopWords = new ArrayList<>();
      wfapp.registerForLoadEvent(this::load);
    }

    public void load(String filename) {
      try {
        Path p = Path.of("src/stop_words.txt");
        stopWords.addAll(Arrays.asList(Files.readString(p).split(",")));
      } catch (IOException ie) {
        ie.printStackTrace();
      }
    }

    public boolean isStopWord(String word){
      return stopWords.contains(word);
    }
  }


  public static class wordFrequencyCounter {

    private Map<String, Integer> wfMap;

    public wordFrequencyCounter(wordFrequencyFramework wfapp, dataStorage ds) {
      this.wfMap = new HashMap<>();
      ds.registerForWordEvent(this::incrementCount);
      wfapp.registerForEndEvent(this::printTop25);
    }

    public void incrementCount(String str) {
      this.wfMap.put(str, this.wfMap.getOrDefault(str, 0) + 1);
    }

    public void printTop25() {
      List<Map.Entry<String, Integer>> list = new ArrayList<>(this.wfMap.entrySet());
      list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      for (int i = 0; i < 25; i++) {
        System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
      }
    }
  }

  public static class wordsWithz{
    private int count;
    private Set<String> set;
    stopWordFilter swFilter;

    public wordsWithz(wordFrequencyFramework wfapp, dataStorage ds, stopWordFilter swFilter){
      this.count = 0;
      this.set = new HashSet<>();
      this.swFilter = swFilter;
      ds.registerForWordEvent(this::count);
      wfapp.registerForEndEvent(this::print);
    }

    public void count(String word){
      if(!swFilter.isStopWord(word) && word.contains("z")){
        count++;
        this.set.add(word);
      }
    }

    public void print(){
      System.out.println("Number of words containing z: " + this.count);
      System.out.println("Number of words containing z after removing their duplicates: " + this.set.size());
    }
  }


  public static void main(String[] args) {
    wordFrequencyFramework wfapp = new wordFrequencyFramework();
    stopWordFilter swFilter = new stopWordFilter(wfapp);
    dataStorage ds = new dataStorage(wfapp, swFilter);
    wordFrequencyCounter wfCounter = new wordFrequencyCounter(wfapp, ds);
    wordsWithz wordsWithz = new wordsWithz(wfapp, ds, swFilter);

    wfapp.run("pride-and-prejudice.txt");
  }

}
