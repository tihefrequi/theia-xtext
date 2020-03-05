package {service.namespace}.jpa.converter;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The converter is required for fields marked with Temporal.DATE, because by read we should  assume the UTC time zone, but receive 
 * the timestamp in the system default time zone
 * @author storm
 *
 */
@Converter
public class DateUTCConverter  implements AttributeConverter<Calendar,Date>{

  @Override
  public Date convertToDatabaseColumn(Calendar calender) {    
    
    //on the way to the database we save the point of time as is, 
    //since we assume that it's correct independent of the time zone of the calender (usually should be UTC)
    if(calender != null) {
      return new Date(calender.getTimeInMillis());
    } else {
      return null;
    }
  }

  @Override
  public Calendar convertToEntityAttribute(Date date) {
    if(date != null) {
      //the ms in the timestamp we get from the DB represent the point of time at the system default time zone. The Hibernate hast the property hibernate.jdbc.time_zone to set the default timezone used, but here we should care ourself about this 
      //so we construct the date time object with system default time zone information and reset the time zone to the UTC without changing the time.
      //to receive the amount of ms in the correct time zone as we saved (UTC) 
      //I.e. we just move the point of time to the other time zone (see also https://stackoverflow.com/questions/41004755/change-timezone-of-date-object-without-changing-date)
      ZonedDateTime zdtLocal = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault());
      ZonedDateTime zdtUTC = zdtLocal.withZoneSameLocal(ZoneId.of("UTC"));
      
      Calendar calendar = GregorianCalendar.from(zdtUTC);
      
      return calendar;
    } else {
      return null;
    }
  }
  

}
