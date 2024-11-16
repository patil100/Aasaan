package Functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
boolean isValidDate(String inDate)  //Returns true if date is in the mentioned format and false if not
boolean compareDates(String d1,String d2)  //compares 2 dates and checks if date 1 comes after date 2 or are equal, returns false if neither
 */

public class DateValidate {
    public static boolean isValidDate(String inDate, String requiredFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(requiredFormat);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean compareDates(String d1,String d2)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY MMM DD HH:mm:ss");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);


            if(date1.after(date2)||date1.equals(date2)){
                return true;
            }
            return false;

        }catch(ParseException ex){
            ex.printStackTrace();
            return false;
        }
    }
}
