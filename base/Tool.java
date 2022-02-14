package base;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Tool implements Serializable {
    public String toUpperFirst(String string){
        
        string = string.trim();

        char[] segment = string.toCharArray();
        String first = String.valueOf(segment[0]);

        first = first.toUpperCase();
        segment[0] = first.charAt(0);

        String finalString = new String(segment);

        return finalString;
        
    }

    public boolean isNumber(String string){

        try {

            Double.parseDouble(string);
            return true;

        } catch (Exception e) {

            return false;

        }

    } 
    

    public static double getRandomNumber(int min, int max) {

        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;

    }
    
    
    public static Date convert(java.util.Date date){
    
        return new Date(date.getYear(),date.getMonth(),date.getDate());
    
    }
    
    
    public static int getNumeroSemaine(Date date){
        
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.set(date.getYear() ,date.getMonth() ,date.getDate() );
        int numSemaine = calendar.get(Calendar.WEEK_OF_YEAR);
        
        return numSemaine;
        
    }
    
    public static int getNumeroSemaineMois(Date date){
        
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.set(date.getYear() ,date.getMonth() ,date.getDate() );
        int numSemaine = calendar.get(Calendar.WEEK_OF_MONTH);
        
        return numSemaine;
        
    }
    
    public static String getMonthNameOfDate(Date date){
        Calendar mCalendar = Calendar.getInstance();    
        mCalendar.set(Calendar.MONTH, date.getMonth());
        String month = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        return month;
    }
    
    
    public static Date getAlahadyAvecNumSemaineAndAnnee(int numSemaine,int annee){
        
        return Tool.getDateAvecNumSemaineAndAnneeAndJour(numSemaine, annee, Calendar.SUNDAY);
        
    }
    
    public static Date getThisAlahady(){
        
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        
        int numSemaine = calendar.get(Calendar.WEEK_OF_YEAR);
        int annee = calendar.get(Calendar.YEAR);
        
        return Tool.getDateAvecNumSemaineAndAnneeAndJour(numSemaine, annee, Calendar.SUNDAY);
        
    }
    
    public static Date getDateAvecNumSemaineAndAnneeAndJour(int numSemaine,int annee, int jour){
        
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        
        calendar.set(Calendar.YEAR, annee);
        calendar.set(Calendar.WEEK_OF_YEAR, numSemaine);
        calendar.set(Calendar.DAY_OF_WEEK, jour);

        return Tool.convert(calendar.getTime());
        
    }
    
    public static Date getToday(){
        
        Calendar calendar = Calendar.getInstance();
        return Tool.convert(calendar.getTime());
        
    }
    
    public static long dayBetweenTwoDates(Date date1, Date date2){
    
        long diff = date2.getTime() - date1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    
    }

}
