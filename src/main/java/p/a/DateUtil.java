package p.a;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Mr.zyl
 */
public class DateUtil {
    static String startTime = null;

    /**
     * @param calendar
     * @param formater eg:yyyyMMdd HH:mm:ss:SSS
     * @return
     */
    public static String calendarToString(Calendar calendar, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    /**
     * @param dateStr
     * @param formater
     * @return
     * @throws ParseException
     */
    public static Calendar stringToCalendar(String dateStr, String formater)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        Date date = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param date
     * @param formater
     * @return
     */
    public static String dateToString(Date date, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * @param dateStr
     * @param formater
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String dateStr, String formater)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        Date date = sdf.parse(dateStr);
        return date;
    }

    /**
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param calendar
     * @return
     */
    public static Date calendarToDate(Calendar calendar) {
        Date date = calendar.getTime();
        return date;
    }

    /**
     * @param dateStr eg:yyyyMMdd HH:mm:ss:SSS
     * @return
     */
    public static Timestamp stringToTimestamp(String dateStr) {
        Timestamp ts = Timestamp.valueOf(dateStr);
        return ts;
    }

    /**
     * @param date
     * @param formater eg:yyyyMMdd HH:mm:ss:SSS
     * @return
     */
    public static Timestamp dateToTimestamp(Date date, String formater) {
        SimpleDateFormat df = new SimpleDateFormat(formater);
        String time = df.format(date);
        Timestamp ts = Timestamp.valueOf(time);
        return ts;
    }

    /**
     * 得到当前时间
     *
     * @param formater eg:yyyyMMdd HH:mm:ss
     * @return
     */
    public static String getCurrentDate(String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(new Date());
    }

    public static String startCurrentDate(String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        if (startTime == null) {
            startTime = sdf.format(new Date());
        }
        return startTime;
    }

    /**
     * @param str
     * @return
     * @throws Exception
     */
    public static int getWeekOfYear(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        Calendar calendar = Calendar.getInstance();
        try {
            date = sdf.parse(dateStr);
            calendar.setTime(date);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            return week;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param dateStr
     * @return
     * @author Mr.zyl
     */
    public static int getWeekOfMonth(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        Calendar calendar = Calendar.getInstance();
        try {
            date = sdf.parse(dateStr);
            calendar.setTime(date);
            int week = calendar.get(Calendar.WEEK_OF_MONTH);
            return week;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param dateStr
     * @return
     * @author Mr.zyl
     */
    public static int getDayOfWeek(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        Calendar calendar = Calendar.getInstance();
        try {
            date = sdf.parse(dateStr);
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            return day;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}