package Week2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;


public class Four {

  public static void main(String[] args) throws IOException {
    String str_stop = Files.readString(Path.of("src/stop_words.txt"));
    String book = Files.readString(Path.of("src/pride-and-prejudice.txt"));
    List<String> stopWords = List.of(str_stop.split(","));

    String[] words = book.toLowerCase().replaceAll("[^a-z]+", " ").split("[\\s+]");
    List<String> tidyWords = new LinkedList<>();
    for (String word : words) {
      if (word.matches("[a-z]{2,}") && !stopWords.contains(word)) {
        tidyWords.add(word);
      }
    }

    List<String> termSet = new LinkedList<>();
    List<Integer> freq = new LinkedList<>();
    for (String word : tidyWords) {
      if (!termSet.contains(word)) {
        termSet.add(word);
        freq.add(1);
      } else {
        freq.set(termSet.indexOf(word), freq.get(termSet.indexOf(word)) + 1);
      }
    }

    List<tfPair> tfPairs = new LinkedList<>();
    for (String term : termSet)
      tfPairs.add(new tfPair(term, freq.get(termSet.indexOf(term))));
    tfPairs.sort(Comparator.reverseOrder());

//    List<tfPair> orderedPairs = new LinkedList<>();
//    for(int i = 0; i < tfPairs.size() - 1; i++){
//      for (int j = 0; j < tfPairs.size() - i - 1; j++){
//        tfPair pair1 = tfPairs.get(j);
//        tfPair pair2 = tfPairs.get(j + 1);
//        if (pair1.compareTo(pair2) < 0){
//          tfPair tempPair = pair1;
//          tfPairs.set(i, pair2);
//          tfPairs.set(j, tempPair);
//        }
//      }
//    }

    for (int i = 0; i < 25; i++) {
      tfPair pair = tfPairs.get(i);
      System.out.println(pair.getWord() + "  -  " + pair.getFrequency());
    }
  }

  private static class tfPair implements Comparable<tfPair> {

    private String word;
    private int frequency;

    private tfPair(String word, int frequency) {
      this.word = word;
      this.frequency = frequency;
    }

    private String getWord() {
      return word;
    }

    private int getFrequency() {
      return frequency;
    }

    @Override
    public int compareTo(tfPair o) {
      return this.frequency - o.frequency;
    }
  }
}
