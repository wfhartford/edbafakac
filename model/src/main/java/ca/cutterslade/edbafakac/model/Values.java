package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;

public final class Values {

  private Values() {
    throw new UnsupportedOperationException();
  }

  private abstract static class ServiceHolder {

    private static final EntryService ENTRY_SERVICE = EntryServiceFactory.INSTANCE.getEntryService();

    public static EntryService getEntryService() {
      return ENTRY_SERVICE;
    }
  }

  private static EntryService getEntryService() {
    return ServiceHolder.getEntryService();
  }

  public static Entry getNewEntry() {
    return getEntryService().getNewEntry();
  }

  public static Value getNewValue(final TypeValue type) {
    final Entry entry = getNewEntry();
    entry.setProperty(BaseField.VALUE_TYPE.getKey(), type.getKey());
    entry.setProperty(BaseField.VALUE_CLASS.getKey(), BaseField.TYPE_CLASS.getField().getRawValue(type));
    return Value.getInstance(entry);
  }

  public static <T> T getValue(final String key, final Class<T> clazz) {
    // TODO
    return null;
  }

  public static <T> T getValue(final String key, final Class<T> clazz, final String defaultResource) {
    // TODO
    return getValue(key, clazz);
  }
}
