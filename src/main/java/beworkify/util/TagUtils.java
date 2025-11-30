package beworkify.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagUtils {

  private static final Pattern HASHTAG = Pattern.compile("#([A-Za-z0-9_-]+)");

  public static String extractTagsAsPipe(String content) {
    if (content == null) return "";
    Matcher m = HASHTAG.matcher(content);
    Set<String> tags = new LinkedHashSet<>();
    while (m.find()) {
      String t = m.group(1).toLowerCase();
      if (!t.matches("\\d+")) {
        tags.add(t);
      }
    }
    return tags.stream().map(s -> "|" + s + "|").collect(Collectors.joining(""));
  }
}
