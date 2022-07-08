package Week1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class AzarUmudov_solution {
  public static void main(String[] args) throws IOException{
    Path stopword_file = Path.of("src/stop_words.txt");
    String stopwords_str = Files.readString(stopword_file);
    List<String> stopwords = new ArrayList<>(Arrays.asList(stopwords_str.split(",")));
    Path file = Path.of("src/pride-and-prejudice.txt");
    String words = Files.readString(file).toLowerCase();

    List<String> listOfWords = new ArrayList<String>();
    Matcher m = Pattern.compile("[a-z]{2,}").matcher(words);

    while (m.find()) {
      if (!stopwords.contains(m.group())){
        listOfWords.add(m.group());
      }
    }

    HashMap<String,Integer> frequencyMap = new HashMap<String,Integer>();
    for(String word: listOfWords) {
      if(frequencyMap.containsKey(word)) {
        frequencyMap.put(word, frequencyMap.get(word)+1);
      }
      else{ frequencyMap.put(word, 1); }
    }

    LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

    frequencyMap.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(25)
        .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

    for (Map.Entry<String, Integer> pair : reverseSortedMap.entrySet()) {
      System.out.println(String.format("%s - %s", pair.getKey(), pair.getValue()));
    }
  }
}
