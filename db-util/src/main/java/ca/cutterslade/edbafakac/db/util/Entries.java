package ca.cutterslade.edbafakac.db.util;

import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;

public final class Entries {

  private static final char ESCAPE_CHAR = '\\';

  private Entries() {
    throw new UnsupportedOperationException();
  }

  public static String exportEntry(final Entry entry) {
    return exportEntry(entry, new StringBuilder()).toString();
  }

  public static StringBuilder exportEntry(final Entry entry, final StringBuilder builder) {
    toCsvValue(builder, entry.getKey());
    for (final Map.Entry<String, String> property : entry.getProperties().entrySet()) {
      builder.append(',');
      toCsvValue(builder, property.getKey());
      builder.append(',');
      toCsvValue(builder, property.getValue());
    }
    return builder;
  }

  @SuppressWarnings("PMD.MissingBreakInSwitch")
  private static void toCsvValue(final StringBuilder builder, final String value) {
    // CSOFF: ModifiedControlVariable
    for (char cha : value.toCharArray()) {
      // CSOFF: FallThrough
      switch (cha) {
        case '\n' :
          cha = 'n';
        case ESCAPE_CHAR :
        case ',' :
          builder.append(ESCAPE_CHAR);
        default :
          builder.append(cha);
      }
      // CSON: FallThrough
    }
    // CSON: ModifiedControlVariable
  }

  public static Entry importEntry(final EntryService service, final String entry) {
    final StringBuilder builder = new StringBuilder();
    int position = fromCsvValue(entry, builder, 0);
    final Entry newEntry = service.getNewEntry(builder.toString());
    while (-1 != position) {
      builder.setLength(0);
      position = fromCsvValue(entry, builder, position);
      final String key = builder.toString();
      builder.setLength(0);
      position = fromCsvValue(entry, builder, position);
      newEntry.setProperty(key, builder.toString());
    }
    service.saveEntry(newEntry);
    return newEntry;
  }

  private static int fromCsvValue(final String entry, final StringBuilder builder, final int startPosition) {
    Preconditions.checkArgument(0 <= startPosition);
    boolean escaped = false;
    int endPosition = -1;
    for (int position = startPosition; -1 == endPosition && position < entry.length(); position++) {
      char cha = entry.charAt(position);
      if (!escaped && ESCAPE_CHAR == cha) {
        escaped = true;
      }
      else {
        if (escaped) {
          escaped = false;
          // CSOFF: NestedIfDepth
          if ('n' == cha) {
            cha = '\n';
          }
          // CSON: NestedIfDepth
        }
        else if (',' == cha) {
          endPosition = position;
        }
        builder.append(cha);
      }
    }
    return endPosition;
  }
}
