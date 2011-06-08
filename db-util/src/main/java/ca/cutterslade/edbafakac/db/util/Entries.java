package ca.cutterslade.edbafakac.db.util;

import java.io.IOException;
import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public final class Entries {

  private static final char ESCAPE_CHAR = '\\';

  private static final char VALUE_SEPERATOR = ',';

  private static final char RECORD_SEPERATOR = '\n';

  private Entries() {
    throw new UnsupportedOperationException();
  }

  public static void exportEntries(final Iterable<? extends Entry> entries, final Appendable writer)
      throws IOException {
    for (final Entry entry : entries) {
      exportEntry(entry, writer);
      writer.append(RECORD_SEPERATOR);
    }
  }

  public static String exportEntry(final Entry entry) {
    try {
      return exportEntry(entry, new StringBuilder()).toString();
    }
    catch (final IOException e) {
      // Should not get an IOException appending to a string builder
      throw Throwables.propagate(e);
    }
  }

  public static <T extends Appendable> T exportEntry(final Entry entry, final T builder) throws IOException {
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
  private static void toCsvValue(final Appendable builder, final String value) throws IOException {
    // CSOFF: ModifiedControlVariable
    for (final char cha : value.toCharArray()) {
      // CSOFF: FallThrough
      switch (cha) {
        case RECORD_SEPERATOR :
        case ESCAPE_CHAR :
        case VALUE_SEPERATOR :
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
      final char cha = entry.charAt(position);
      if (!escaped && ESCAPE_CHAR == cha) {
        escaped = true;
      }
      else if (!escaped && (VALUE_SEPERATOR == cha || RECORD_SEPERATOR == cha)) {
        endPosition = position;
      }
      else {
        escaped = false;
        builder.append(cha);
      }
    }
    return endPosition;
  }
}
