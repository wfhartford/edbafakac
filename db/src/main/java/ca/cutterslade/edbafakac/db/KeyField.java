package ca.cutterslade.edbafakac.db;

import java.util.Map;

import com.mongodb.BasicDBObject;

final class KeyField extends Field<String> {

  KeyField(final Map<String, Object> values, final Configuration configuration) {
    super(new BasicDBObject(values), configuration, false);
  }

  @Override
  protected String getFieldKey() {
    return Fields.KEY_FIELD_KEY;
  }

}
