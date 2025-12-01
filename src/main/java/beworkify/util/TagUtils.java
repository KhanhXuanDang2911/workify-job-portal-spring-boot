package beworkify.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for processing hashtags in text content. Extracts hashtags and formats them for
 * storage or processing.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
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
