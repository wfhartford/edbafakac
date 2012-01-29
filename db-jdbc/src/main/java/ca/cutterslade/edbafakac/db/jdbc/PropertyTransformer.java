package ca.cutterslade.edbafakac.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import ca.cutterslade.edbafakac.db.jdbc.JdbcEntryService.Property;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

enum PropertyTransformer implements Function<ResultSet, Property> {
  INSTANCE;

  @Override
  public Property apply(final ResultSet input) {
    if (null == input) {
      throw new IllegalArgumentException();
    }
    try {
      return new Property(input.getString("PROPERTY_KEY"), input.getString("PROPERTY_VALUE"));
    }
    catch (final SQLException e) {
      throw Throwables.propagate(e);
    }
  }
}
