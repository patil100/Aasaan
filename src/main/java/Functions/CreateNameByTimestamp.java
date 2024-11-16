package Functions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CreateNameByTimestamp {
    public static String getTimestamp(String dateFormat) {
        String timeStamp = new SimpleDateFormat(dateFormat).format(new Date());
        return timeStamp;
    }
    public static String getTimestamp() {
        String dateFormat="yyyy-MM-dd_HH:mm:ss";
        String timeStamp = new SimpleDateFormat(dateFormat).format(new Date());
        return timeStamp;
    }
    public static String getTimestampShort() {
        String dateFormat="ddHHmmss";
        String timeStamp = new SimpleDateFormat(dateFormat).format(new Date());
        return timeStamp;
    }
    public static String getRandomString() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        // create an object of Random class
        Random random = new Random();
        // specify length of random string
        int length=6;
        for(int i = 0; i < length; i++) {
            // generate random index number
            int index = random.nextInt(alphabet.length());
            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);
            // append the character to string builder
            sb.append(randomChar);
        }
        String randomString = sb.toString();
        return  randomString;
    }
}