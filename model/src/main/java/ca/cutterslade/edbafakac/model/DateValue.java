package ca.cutterslade.edbafakac.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class DateValue extends Value<DateValue> {

  private static final String TIME_KEY = "b61a1f11-f434-4d1f-952e-97dfaba65af1";

  private static final String ZONE_KEY = "999a2b99-c0bb-4bcb-912d-02a3f043f3e2";

  DateValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }

  public DateValue setValue(@Nullable final Date date, @Nullable final TimeZone zone) {
    Preconditions.checkArgument((null == date) == (null == zone), "date and zone must have equal nullity");
    Preconditions.checkArgument(null == zone || zone.equals(TimeZone.getTimeZone(zone.getID())),
        "zone must be equal to that retrieved by its ID");
    return null == date ? removeProperty(TIME_KEY).removeProperty(ZONE_KEY) :
        setProperty(TIME_KEY, String.valueOf(date.getTime())).setProperty(ZONE_KEY, zone.getID());
  }

  public DateValue setValue(@Nullable final Calendar calendar) {
    return null == calendar ? setValue(null, null) : setValue(calendar.getTime(), calendar.getTimeZone());
  }

  public Date getDate() {
    final String value = getProperty(TIME_KEY);
    return null == value ? null : new Date(Long.valueOf(value));
  }

  public TimeZone getZone() {
    final String value = getProperty(ZONE_KEY);
    return null == value ? null : TimeZone.getTimeZone(value);
  }

  public Calendar getCalendar(@Nullable final Locale locale) {
    final Date date = getDate();
    final Calendar calendar;
    if (null == date) {
      calendar = null;
    }
    else {
      calendar = null == locale ? new GregorianCalendar(getZone()) : new GregorianCalendar(getZone(), locale);
      calendar.setTime(date);
    }
    return calendar;
  }
}
