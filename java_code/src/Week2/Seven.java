package Week2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Seven {

  public static void main(String[] args) throws IOException {
    List<String> stopWords = List.of(Files.readString(Path.of("src/stop_words.txt")).split(","));
    List<String> book = List.of(Files.readString(Path.of("src/pride-and-prejudice.txt")));
    book.stream().flatMap(line -> Arrays.stream(line.toLowerCase().replaceAll("[^a-z]+", " ").split("[\\s+]")).filter(w -> w.matches("[a-z]{2,}")).filter(w -> !stopWords.contains(w)))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(25).forEachOrdered(pair -> System.out.println(pair.getKey() + "  -  " + pair.getValue()));
  }
}
