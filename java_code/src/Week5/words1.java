package Week5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class words1 implements Function<String, List<String>> {

  @Override
  public List<String> apply(String book) {

    List<String> stopWords = new ArrayList<>();
    try {
      Path swPath = Path.of("src/stop_words.txt");
      String[] words = Files.readString(swPath).split(",");
      stopWords.addAll(List.of(words));
    } catch (IOException ie) {
      ie.printStackTrace();
    }

    Path bookPath = Path.of(book);
    String file = "";
    try {
      file = Files.readString(bookPath).toLowerCase();
    } catch (IOException ie) {
      ie.printStackTrace();
    }
    Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(file);
    List<String> wordList = new ArrayList<>();
    while (matcher.find() && !stopWords.contains(matcher.group())) {
      wordList.add(matcher.group());
    }
    return wordList;
  }
}
