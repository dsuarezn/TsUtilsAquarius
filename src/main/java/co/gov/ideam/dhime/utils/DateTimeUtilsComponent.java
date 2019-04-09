package co.gov.ideam.dhime.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateTimeUtilsComponent {

	
	
	public static Date parseTsDateOffsetToCurrentTimeZone(String fecha) {
        Date date=null;
        try {
        	DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        	utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        	utcFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
//        	utcFormat.setTimeZone(TimeZone.getDefault());
        	utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

//        	DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        	pstFormat.setTimeZone(TimeZone.getDefault());            

        	 date = utcFormat.parse(fecha);            
        }catch (Exception e){ e.printStackTrace();}
        return date;
	}

	public static Date parseCurrentTimeZoneToDateOffset(String fecha) {
        Date date=null;
        try {
        	DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        	utcFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
        	utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));            

//        	DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        	pstFormat.setTimeZone(TimeZone.getDefault());            

        	 date = utcFormat.parse(fecha);            
        }catch (Exception e){ e.printStackTrace();}
        return date;
	}
	
	public static Date parseDateToCompareFormat(String date) throws ParseException{
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		return dateFormat.parse(date);		
	}
	
	public static String formatDateToCompareFormat(Date date){
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		return dateFormat.format(date);		
	}
	
	public static String formatDateToShortCompareFormat(Date date){
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		return dateFormat.format(date);		
	}
	
	public static Date parseFromDBIdeam(String date, boolean leap) throws ParseException{			
		DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	
		dateFormat.setLenient(leap);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		return dateFormat.parse(date);		
	}
	
	public static String formatFromDBIdeam(Date fecha) throws ParseException{		
		DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		return dateFormat.format(fecha);		
	}
	
	public static String formatDateOffset(Instant instant) {
        String date=null;
        try {
        	DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	pstFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));          

        	org.joda.time.Instant myInstant = new org.joda.time.Instant( instant.toEpochMilli() ); 
        	 date = pstFormat.format(myInstant.toDate());  
        	 System.out.println(date);
        }catch (Exception e){ e.printStackTrace();}
        return date;
	}
	
//	@Test
//	public void testFormat() throws ParseException{
//		Date fecha=parseFromDBIdeam("12/29/2008 13:00:00");
//		System.out.println(formatDateToCompareFormat(fecha));			
//
//	}
	
	public static Date addHours(Date fecha, Integer hours){
		Calendar cal = Calendar.getInstance();
		// remove next line if you're always using the current time.
		cal.setTime(fecha);
		cal.add(Calendar.HOUR, hours);
		return  cal.getTime();
	}
	
	
}
