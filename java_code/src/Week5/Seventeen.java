package Week5;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seventeen {

  public static void main(String[] args) {
    try{
      wordFrequencyController wfController = new wordFrequencyController();
      wfController.run("pride-and-prejudice.txt");
      System.out.println("------------------------------");

      //Print reflection results
      printClassFields("Week5.dataStorageManager");
      printClassFields("Week5.stopWordManager");
      printClassFields("Week5.wordFrequencyManager");
      printClassFields("Week5.wordFrequencyController");

    } catch(Exception e){
      e.printStackTrace();
    }
  }


  private static void printClassFields(String classname) throws ClassNotFoundException {
    Class cls = Class.forName(classname);
    Field[] fields = cls.getDeclaredFields();
    Method[] methods = cls.getMethods();
    Class superClass = cls.getSuperclass();
    Class[] interfaces = cls.getInterfaces();

    System.out.println("Reflection for " + cls.getName() + " :******");
    System.out.println("Fields:");
    for (Field f : fields){
      System.out.println("Field Name: " + f.getName() + "  Field Type: " + f.getType());
    }
    System.out.println("---------------------------");

    System.out.println("Methods:");
    for (Method m : methods){
      System.out.println(m.getName());
    }
    System.out.println("----------------------------");

    System.out.println("Superclasses:");
    while(superClass != null){
      System.out.println(superClass.getName());
      superClass = superClass.getSuperclass();
    }
    System.out.println("-----------------------------");

    System.out.println("Interfaces:");
    for (Class itf : interfaces){
      System.out.println(itf.getName());
    }
    System.out.println();
  }

}


interface DSI{
  List<String> words(String file) throws IOException;
}

interface SWMI{
  boolean isStopWord(String word);
}


interface WFMI{
  void count(String word);
  void printTop25();
}

class wordFrequencyController{
  private dataStorageManager ds;
  private stopWordManager swm;
  private wordFrequencyManager wfm;
  private Class dsClass;
  private Class swmClass;
  private Class wfmClass;

  public wordFrequencyController() {
    try {
      dsClass = Class.forName("Week5.dataStorageManager");
      swmClass = Class.forName("Week5.stopWordManager");
      wfmClass = Class.forName("Week5.wordFrequencyManager");
      ds = (dataStorageManager) dsClass.getDeclaredConstructor().newInstance();
      swm = (stopWordManager) swmClass.getDeclaredConstructor().newInstance();
      wfm = (wordFrequencyManager) wfmClass.getDeclaredConstructor().newInstance();
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  public void run(String filename) {
    try {
      for (String word : (List<String>) dsClass.getDeclaredMethod("words", String.class).invoke(ds, filename)) {
        if (!(boolean) swmClass.getDeclaredMethod("isStopWord", String.class)
            .invoke(swm, word)) {
          wfmClass.getDeclaredMethod("count", String.class).invoke(wfm, word);
        }
      }

      wfmClass.getDeclaredMethod("printTop25").invoke(wfm);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}

class dataStorageManager implements DSI{
  private String book;

  public dataStorageManager(){
    book = "";
  }

  @Override
  public List<String> words(String filename) throws IOException {
    Path path = Path.of("src/", filename);
    String file = Files.readString(path);
    book = file.toLowerCase();
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(book);
    List<String> wordList = new ArrayList<>();
    while (matcher.find())
      wordList.add(matcher.group());
    return wordList;
  }
}

class stopWordManager implements SWMI{

  private List<String> stopWords;

  public stopWordManager() throws IOException {
    stopWords = new ArrayList<>();
    Path path = Path.of("src/stop_words.txt");
    String sw = Files.readString(path);
    stopWords = List.of(sw.split(","));
  }

  @Override
  public boolean isStopWord(String word) {
    return stopWords.contains(word);
  }
}

class wordFrequencyManager implements WFMI{

  private Map<String, Integer> tfMap;

  public wordFrequencyManager() {
    tfMap = new HashMap<>();
  }

  @Override
  public void count(String word) {
    tfMap.put(word, tfMap.getOrDefault(word, 0) + 1);
  }

  @Override
  public void printTop25() {
    List<Map.Entry<String, Integer>> sortedPairs = new ArrayList<>(tfMap.entrySet());
    sortedPairs.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    for (int i = 0; i < 25; i++) {
      System.out.println(sortedPairs.get(i).getKey() + "  -  " + sortedPairs.get(i).getValue());
    }
  }
}

