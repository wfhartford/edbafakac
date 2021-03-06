package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class DateValueTest extends ValueServiceTest {

  @Test
  public void calendarSaveLoadTest() {
    final Locale locale = Locale.getDefault();
    final Calendar calendar = Calendar.getInstance(locale);
    DateValue dateValue = getValueService().dateWithTime(calendar).save();
    dateValue = (DateValue) getValueService().getValue(dateValue.getKey(), RetrieveMode.READ_ONLY);
    assertEquals(calendar.getTime(), dateValue.getDate());
    assertEquals(calendar.getTimeZone(), dateValue.getZone());
    assertEquals(calendar, dateValue.getCalendar(locale));
  }

  @Test
  public void dateZoneSaveLoadTest() {
    final Calendar calendar = Calendar.getInstance();
    DateValue dateValue = getValueService().dateWithTime(calendar.getTime(), calendar.getTimeZone()).save();
    dateValue = (DateValue) getValueService().getValue(dateValue.getKey(), RetrieveMode.READ_ONLY);
    assertEquals(calendar.getTime(), dateValue.getDate());
    assertEquals(calendar.getTimeZone(), dateValue.getZone());
    assertEquals(calendar, dateValue.getCalendar(null));
  }

  @Test
  public void nullSaveLoadTest() {
    DateValue dateValue = getValueService().dateWithTime(null).save();
    dateValue = (DateValue) getValueService().getValue(dateValue.getKey(), RetrieveMode.READ_ONLY);
    assertNull(dateValue.getDate());
    assertNull(dateValue.getZone());
    assertNull(dateValue.getCalendar(null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullZoneTest() {
    getValueService().dateWithTime(new Date(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullDateTest() {
    getValueService().dateWithTime(null, TimeZone.getDefault());
  }
}
