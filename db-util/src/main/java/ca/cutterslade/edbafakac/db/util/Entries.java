package ca.cutterslade.edbafakac.db.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public final class Entries {

  private static final char ESCAPE_CHAR = '\\';

  private static final char VALUE_SEPERATOR = ',';

  private static final char RECORD_SEPERATOR = '\n';

  private Entries() {
    throw new UnsupportedOperationException();
  }

  public static void exportEntries(@Nonnull final Iterable<? extends Entry> entries,
      @Nonnull final OutputSupplier<? extends OutputStream> stream) throws IOException {
    try (final OutputStream output = stream.getOutput();
        final Writer writer = new OutputStreamWriter(output, Charsets.UTF_8);
        final BufferedWriter bufferedWriter = new BufferedWriter(writer);) {
      exportEntries(entries, writer);
    }
  }

  public static void exportEntries(@Nonnull final Iterable<? extends Entry> entries, @Nonnull final Appendable writer)
      throws IOException {
    for (final Entry entry : entries) {
      exportEntry(entry, writer);
    }
  }

  public static String exportEntry(@Nonnull final Entry entry) {
    try {
      return exportEntry(entry, new StringBuilder()).toString();
    }
    catch (final IOException e) {
      // Should not get an IOException appending to a string builder
      throw Throwables.propagate(e);
    }
  }

  public static <T extends Appendable> T exportEntry(@Nonnull final Entry entry, @Nonnull final T builder)
      throws IOException {
    toCsvValue(builder, entry.getKey());
    for (final Map.Entry<String, String> property : entry.getProperties().entrySet()) {
      builder.append(',');
      toCsvValue(builder, property.getKey());
      builder.append(',');
      toCsvValue(builder, property.getValue());
    }
    builder.append(RECORD_SEPERATOR);
    return builder;
  }

  @SuppressWarnings("PMD.MissingBreakInSwitch")
  private static void toCsvValue(@Nonnull final Appendable builder, @Nonnull final String value) throws IOException {
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

  public static ImmutableList<String> importEntries(@Nonnull final EntryService service,
      @Nonnull final InputSupplier<? extends InputStream> stream) throws IOException {
    try (final InputStream input = stream.getInput();
        final InputStreamReader streamReader = new InputStreamReader(input, Charsets.UTF_8);
        final BufferedReader reader = new BufferedReader(streamReader);) {
      final ImmutableList.Builder<String> builder = ImmutableList.builder();
      for (String line = reader.readLine(); null != line; line = reader.readLine()) {
        builder.add(importEntry(service, line).getKey());
      }
      return builder.build();
    }
  }

  public static ImmutableList<String> importEntries(@Nonnull final EntryService service,
      @Nonnull final Iterable<String> entries) {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (final String entry : entries) {
      builder.add(importEntry(service, entry).getKey());
    }
    return builder.build();
  }

  public static Entry importEntry(@Nonnull final EntryService service, @Nonnull final String entry) {
    final StringBuilder builder = new StringBuilder();
    int position = fromCsvValue(entry, builder, 0);
    final Entry newEntry = service.getNewEntry(builder.toString());
    Long writeTime = null;
    while (-1 != position) {
      builder.setLength(0);
      position = fromCsvValue(entry, builder, position);
      final String key = builder.toString();
      builder.setLength(0);
      position = fromCsvValue(entry, builder, position);
      if (Entry.WRITE_TIME_KEY.equals(key)) {
        writeTime = Long.valueOf(builder.toString());
      }
      else {
        newEntry.setProperty(key, builder.toString());
      }
    }
    if (null == writeTime) {
      service.saveEntry(newEntry);
    }
    else {
      newEntry.setWriteTime(writeTime);
      service.saveEntryWithoutUpdatingWriteTime(newEntry);
    }
    return newEntry;
  }

  private static int fromCsvValue(@Nonnull final String entry, @Nonnull final StringBuilder builder,
      final int startPosition) {
    Preconditions.checkArgument(0 <= startPosition);
    boolean escaped = false;
    int endPosition = -1;
    for (int position = startPosition; -1 == endPosition && position < entry.length(); position++) {
      final char cha = entry.charAt(position);
      if (!escaped && ESCAPE_CHAR == cha) {
        escaped = true;
      }
      else if (!escaped && (VALUE_SEPERATOR == cha)) {
        endPosition = position + 1;
      }
      else if (!escaped && (RECORD_SEPERATOR == cha)) {
        endPosition = -1;
      }
      else {
        escaped = false;
        builder.append(cha);
      }
    }
    return endPosition;
  }
}
