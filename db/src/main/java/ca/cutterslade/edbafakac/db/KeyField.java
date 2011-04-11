package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

final class KeyField extends Field<String> {

  private static final DBObject VALUES = new ImmutableDBObject();

  KeyField(final Configuration configuration) {
    super(VALUES, configuration);
  }

  @Override
  protected String getFieldKey() {
    return Fields.KEY_FIELD_KEY;
  }

}
