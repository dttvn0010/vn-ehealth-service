/**
 * Copyright (c) 2015 by Open eGovPlatform (http://http://openegovplatform.org/).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.ehealth.hl7.fhir.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/** 
 * Date utility class
 * This class contains method to manipulate date
 * 
 * Version: 1.0
 *  
 * History: 
 *   DATE        AUTHOR      DESCRIPTION 
 *  ------------------------------------------------- 
 *  2-Apr-2015  ThongDV    Create new
 */
public class DateUtil {
    
    public static final String DATE_FORMAT_FULL ="yyyyMMddHH24mmss";
    public static final String DATE_FORMAT_D_M_Y ="dd/MM/yyyy";
    public static final String DATE_FORMAT_D_M_Y_H_M_S ="dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_D_M_Y_H_M ="dd/MM/yyyy HH:mm";    
    public static final String DATE_FORMAT_Y_M_D="yyyyMMdd";
    public static final String DATE_DB_FORMAT_Y_M_D="yyyy-MM-dd";
    public static final String DATE_DB_FORMAT_Y_M_D_H="yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_H_M_D_M_Y="HH:mm dd/MM/yyyy";        
    public static final String DATE_FORMAT_YYYY="yyyy";        
    /** 
     * This is method parse format date to string
     * 
     * Version: OEP 1.1
     *  
     * History: 
     *   DATE        AUTHOR      DESCRIPTION 
     *  ------------------------------------------------- 
     *  26-March-2015  ThongDV    Create new
     * @param date
     * @param format
     * @return string 
     * @copy Son.vu 
     */
    public static String parseDateToString(Date date,String format)    {
        try {
            SimpleDateFormat sdf= new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }    
    
    
    /** 
     * This is method parse format string to date
     * 
     * Version: OEP 1.1
     *  
     * History: 
     *   DATE        AUTHOR      DESCRIPTION 
     *  ------------------------------------------------- 
     *  26-March-2015  ThongDV    Create new
     * @param strDate
     * @param format
     * @return Date
     * @copy Son.vu
     */
    public static Date parseStringToDate(String strDate,String format){
        try {
            SimpleDateFormat sdf= new SimpleDateFormat(format);
            return sdf.parse(strDate);
        } catch (Exception e) {
        }
        return null;
    }
    
    /** 
     * This is method get current date time
     * 
     * Version: OEP 1.1
     *  
     * History: 
     *   DATE        AUTHOR      DESCRIPTION 
     *  ------------------------------------------------- 
     *  26-March-2015  ThongDV    Create new
     * @return Date
     * @copy Son.vu
     */
    public static Date getCurrentDateTime(){
        return new Date();
    }
    
    /** 
     * This is method to check validate date time input
     * 
     * Version: OEP 1.1
     *  
     * History: 
     *   DATE        AUTHOR      DESCRIPTION 
     *  ------------------------------------------------- 
     *  17-Nov-2015  DuongPT    Create new
     * @return Date
     * @copy Son.vu
     */
    public static boolean isValidDate(String format, String value) {
        boolean valid = true;
        try {
            DateFormat formatter = new SimpleDateFormat(format);
            formatter.setLenient(false);
            formatter.parse(value);
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }
    
    public static Date getBeginOfMonth(Date date){
        Calendar beginOfMonth = Calendar.getInstance();
        beginOfMonth.setTime(date);
        
        beginOfMonth.set(Calendar.DATE, 1);
        beginOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        beginOfMonth.set(Calendar.MINUTE, 0);
        beginOfMonth.set(Calendar.SECOND, 0);
        beginOfMonth.set(Calendar.MILLISECOND, 0);
        return beginOfMonth.getTime();                
    }
    
    public static int getMaxDateInMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DATE);
    }
    
    public static Date getEndOfMonth(Date date){
        Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.setTime(date);
        
        endOfMonth.set(Calendar.DATE, endOfMonth.getActualMaximum(Calendar.DATE));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);
        endOfMonth.set(Calendar.MILLISECOND, 0);
        return endOfMonth.getTime();
    }    
    public static String formatDate (String date, String initDateFormat, String endDateFormat){
        try{
            Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            return formatter.format(initDate);
        }catch(Exception e){
            
        }
        return "";
    }
    public static List<Integer> getPreviousYear_10() {
        List<Integer> lsYear = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            lsYear.add(getPreviousYear(i));
        }
        return lsYear;
    }
    public static String getPreviousYear_n(int n) {
        String lsYear = "";
        for (int i = 0; i < n; i++) {
            if(i==0){
                lsYear =lsYear + String.valueOf((getPreviousYear(i)));
            }else{
                lsYear =lsYear +","+ String.valueOf((getPreviousYear(i)));
            }
        }
        return lsYear;
    }
    private static int getPreviousYear(int n) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -n);
        return prevYear.get(Calendar.YEAR);
    }
    @SuppressWarnings("unused")
    private static int getRangeYear(int n) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR + 5, -n);
        return prevYear.get(Calendar.YEAR);
    }
    public static List<Integer> getListNextYear(int n) {
        List<Integer> lsYear = new ArrayList<Integer>();
        for (int i = 0; i <=n ; i++) {
            lsYear.add(getNextYear(i));
        }
        return lsYear;
    }
    private static int getNextYear(int n) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, +n);
        return prevYear.get(Calendar.YEAR);
    }
    
    public static List<Integer> getListPreviousAndNextYear(int n){
        List<Integer> lsYear = new ArrayList<Integer>();
        for (int i = n ; i >= 1 ; i--) {
            lsYear.add(getPreviousYear(i));
        }
        for (int i = 0; i <=n ; i++) {
            lsYear.add(getNextYear(i));
        }
        return lsYear;
    }
    public static Date addSubtractYear(Date date, int year) {
        if(date != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, year);
            return calendar.getTime();
        }
        return date;
    }
    public static Date subtractYear(Date date, int year) {
        if(date != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if(year == 1) {
                return calendar.getTime();
            }else {
                calendar.add(Calendar.YEAR, -year);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DATE, 1);
                return calendar.getTime();
            }
        }
        return date;
    }
    public static Date addSubtractMonth(Date date, int month) {
        if(date != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, month);
            return calendar.getTime();
        }
        return date;
    }
    public static int daysBetween(Date dateLarge, Date dateSmall) {
        if(dateLarge != null && dateSmall != null){
            return (int)((dateLarge.getTime() - dateSmall.getTime()) / (1000 * 60 * 60 * 24));
        }
        return -1;
    }
}
