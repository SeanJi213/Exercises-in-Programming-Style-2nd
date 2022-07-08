package Week6;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;


public class TwentyNine {

  public static void main(String[] args) throws Exception {
    WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();
    StopWordManager stopWordManager = new StopWordManager(wordFrequencyManager);
    Sender.send(stopWordManager, new Message("init", ""));
    DataManager dataManager = new DataManager(stopWordManager);
    Sender.send(dataManager, new Message("init", "../pride-and-prejudice.txt"));
    WordFrequencyController wordFrequencyController = new WordFrequencyController();
    Sender.send(wordFrequencyController, new Message("init", dataManager));

    for (Thread t : new Thread[]{wordFrequencyManager, stopWordManager, dataManager, wordFrequencyController}) {
      try {
        t.join();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}


class Message {
  String command;
  Object value;

  public Message(String command, Object value) {
    this.command = command;
    this.value = value;
  }
}


class Sender {
  public static void send(ActiveWFObject receiver, Message message) {
    receiver.queue.offer(message);
  }
}


class ActiveWFObject extends Thread {
  String name;
  Queue<Message> queue;
  boolean stopMe;

  public ActiveWFObject() {
    this.queue = new LinkedBlockingQueue<>();
    this.stopMe = false;
    start();
  }

  @Override
  public void run() {
    while (!this.stopMe) {
      Message message;
      if (queue.isEmpty()) {
        try {
          Thread.sleep(100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        message = queue.poll();
        dispatch(message);
        if (message.command.equals("terminate")) {
          this.stopMe = true;
        }
      }
    }
  }

  public void dispatch(Message message) {}

}



class DataManager extends ActiveWFObject {
  String book;
  StopWordManager stopWordManager;

  public DataManager() {
    this.book = "";
  }

  public DataManager(StopWordManager stopWordManager) {
    this.book = "";
    this.stopWordManager = stopWordManager;
  }

  @Override
  public void dispatch(Message message) {
    String command = message.command;
    if ("init".equals(command)) {
      this.init((String)message.value);
    } else if ("send_word_frequencies".equals(command)) {
      this.processWords((ActiveWFObject)message.value);
    } else {
      Sender.send(this.stopWordManager, message);
    }

  }

  public void init(String path) {
    StringBuilder sb = null;
    try {
      BufferedReader buffReader = new BufferedReader(new FileReader(path));
      sb = new StringBuilder();
      String tmp = "";
      while((tmp = buffReader.readLine()) != null){
        sb.append(tmp).append(" ");
      }
      buffReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.book = sb.toString();
  }

  public void processWords(ActiveWFObject recipient) {
    String[] words = this.book.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
    for (String word: words) {
      Sender.send(stopWordManager, new Message("filter", word));
    }
    Sender.send(this.stopWordManager, new Message("top25", recipient));
  }

}

class StopWordManager extends ActiveWFObject {
  List<String> stopWords;
  WordFrequencyManager wordFrequencyManager;

  public StopWordManager(WordFrequencyManager wordFrequencyManager) {
    this.stopWords = new ArrayList<>();
    this.wordFrequencyManager = wordFrequencyManager;
  }

  @Override
  public void dispatch(Message message) {
    String command = message.command;
    if ("init".equals(command)) {
      this.init();
    } else if ("filter".equals(command)) {
      this.checkStopWords((String)message.value);
    } else {
      Sender.send(this.wordFrequencyManager, message);
    }

  }

  public void init() {
    String path = "../stop_words.txt";
    StringBuilder sb;
    try {
      BufferedReader buffReader = new BufferedReader(new FileReader(path));
      sb = new StringBuilder();
      String tmp;
      while((tmp = buffReader.readLine()) != null){
        String[] arr = tmp.split(",");
        Collections.addAll(this.stopWords, arr);
      }
      buffReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void checkStopWords(String word) {
    if (!this.stopWords.contains(word) && word.length() >= 2) {
      Sender.send(this.wordFrequencyManager, new Message("word", word));
    }
  }
}


class WordFrequencyManager extends ActiveWFObject {
  Map<String, Integer> tfPairs;

  public WordFrequencyManager() {
    this.tfPairs = new HashMap<>();
  }

  @Override
  public void dispatch(Message message) {
    String command = message.command;
    if ("word".equals(message.command)) {
      this.count((String)message.value);
    } else if ("top25".equals(message.command)) {
      this.sorted((ActiveWFObject)message.value);
    }
  }

  public void count(String str) {
    this.tfPairs.put(str, this.tfPairs.getOrDefault(str, 0) + 1);
  }

  public void sorted(ActiveWFObject recipient) {
    List<Map.Entry<String, Integer>> list = new ArrayList<>(this.tfPairs.entrySet());
    list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    Sender.send(recipient, new Message("top25", list));
  }

}

class WordFrequencyController extends ActiveWFObject {
  DataManager dataManager;

  public WordFrequencyController() {
  }

  @Override
  public void dispatch(Message message) {
    String command = message.command;
    if ("init".equals(command)) {
      this.init((DataManager)message.value);
    } else if ("top25".equals(command)) {
      this.display((List<Map.Entry<String, Integer>>)message.value);
    } else {
      throw new IllegalArgumentException("Message not found!!");
    }
  }

  public void init(DataManager dataManager) {
    this.dataManager = dataManager;
    Sender.send(this.dataManager, new Message("send_word_frequencies", this));
  }

  public void display(List<Map.Entry<String, Integer>> list) {
    for (int i = 0; i < 25; i++) {
      System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
    }
    Sender.send(this.dataManager, new Message("terminate", ""));
    this.stopMe = true;
  }
}