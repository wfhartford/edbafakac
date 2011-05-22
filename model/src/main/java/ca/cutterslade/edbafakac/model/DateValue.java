package ca.cutterslade.edbafakac.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public class DateValue extends Value {

  private static final String TIME_KEY = "b61a1f11-f434-4d1f-952e-97dfaba65af1";

  private static final String ZONE_KEY = "999a2b99-c0bb-4bcb-912d-02a3f043f3e2";

  public DateValue() {
    super();
  }

  DateValue(final Entry entry) {
    super(entry);
  }

  public void setValue(final Date date, final TimeZone zone) {
    Preconditions.checkArgument(null == date || null != zone, "Zone must be set when date is set");
    if (null == date) {
      removeProperty(TIME_KEY);
      removeProperty(ZONE_KEY);
    }
    else {
      setProperty(TIME_KEY, String.valueOf(date.getTime()));
      setProperty(ZONE_KEY, zone.getID());
    }
  }

  public void setValue(final Calendar calendar) {
    setValue(calendar.getTime(), calendar.getTimeZone());
  }

  public Date getDate() {
    final String value = getProperty(TIME_KEY);
    return null == value ? null : new Date(Long.valueOf(value));
  }

  public TimeZone getZone() {
    final String value = getProperty(ZONE_KEY);
    return null == value ? null : TimeZone.getTimeZone(value);
  }

  public Calendar getCalendar(final Locale locale) {
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
