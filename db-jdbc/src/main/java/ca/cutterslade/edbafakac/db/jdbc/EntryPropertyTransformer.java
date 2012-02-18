package ca.cutterslade.edbafakac.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import ca.cutterslade.edbafakac.db.EntryStoreException;

import com.google.common.base.Function;

enum EntryPropertyTransformer implements Function<ResultSet, EntryProperty> {
  INSTANCE;

  @Override
  public EntryProperty apply(final ResultSet input) {
    final EntryProperty entryProperty;
    if (null == input) {
      entryProperty = null;
    }
    else {
      try {
        int position = 0;
        final String entryKey = input.getString(++position);
        final String propertyKey = input.getString(++position);
        final String value = input.getString(++position);
        entryProperty = new EntryProperty(entryKey, propertyKey, value);
      }
      catch (final SQLException e) {
        throw new EntryStoreException(e);
      }
    }
    return entryProperty;
  }

}
