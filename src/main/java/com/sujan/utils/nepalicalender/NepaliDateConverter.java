package com.sujan.utils.nepalicalender;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NepaliDateConverter {

    private int nepaliYear, nepaliMonth, nepaliDate;
    private int englishYear, englishMonth, englishDate;
    private int weekDay;

    private final int[] englishMonths, englishLeapMonths;

    private final int[][] nepaliMonths;
    /**
     * only this month names are valid: "Baisakh", "Jestha", "Ashadh",
     * "Shrawan", "Bhadra", "Ashwin", "Kartik", "Mangsir", "Paush", "Mangh",
     * "Falgun", "Chaitra".
     */
    public static final String[] VALID_NEPALI_MONTHS = new String[]{"Baisakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashwin", "Kartik", "Mangsir", "Paush", "Magh", "Falgun", "Chaitra"};

    private static int mimNepaliYear = 1970;
    private static int maxNepalYear = 2098;

    public NepaliDateConverter() {

        //English no of days in months
        englishMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        englishLeapMonths = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


        //Nepali no of days in months
        nepaliMonths = new int[][]{
                //this data are from http://www.ashesh.com.np
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   //1970
                {31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 30, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 30, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 32, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},   //1991

                //this data are from http://nepalicalendar.rat32.com/index.php
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},   //1992
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},   //1999
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},   //2000
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   //2001
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {30, 32, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},
                {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},  //2071
                {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},  //2072
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},  //2073
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},
                {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},
                {31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 32, 31, 32, 30, 31, 30, 30, 29, 30, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30},
                {30, 31, 32, 32, 30, 31, 30, 30, 29, 30, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},    //2090
                {31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30},
                {30, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 31, 30, 29, 30, 30, 30, 30},
                {30, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},
                {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},
                {31, 31, 32, 31, 31, 31, 29, 30, 29, 30, 29, 31},
                {31, 31, 32, 31, 31, 31, 30, 29, 29, 30, 30, 30}    //2099
        };
    }

    public void setCurrentDate() throws Exception {
        //Setting current english Date
        Date dNow = new Date();

        SimpleDateFormat ft = new SimpleDateFormat("yyyy");
        int y = new Integer(ft.format(dNow));
        ft = new SimpleDateFormat("MM");
        int m = new Integer(ft.format(dNow));
        ft = new SimpleDateFormat("dd");
        int d = new Integer(ft.format(dNow));

        setEnglishDate(y, m, d);
    }

    //English to Nepali date conversion
    public void setEnglishDate(int year, int month, int date) throws Exception {
        if (!this.isEnglishRange(year, month, date))
            throw new Exception("Invalid date format.");

        this.englishYear = year;
        this.englishMonth = month;
        this.englishDate = date;

        //Setting nepali reference to 2000/1/1 with english date 1943/4/14
//        this.nepaliYear = 2000;
//        this.nepaliMonth = 1;
//        this.nepaliDate = 1;
        this.nepaliYear = mimNepaliYear;
        this.nepaliMonth = 1;
        this.nepaliDate = 1;

//        int difference = this.getEnglishDateDifference(1943, 4, 14);
        int difference = this.getEnglishDateDifference(1913, 4, 14);

        //Getting nepali year until the difference remains less than 365
        int index = 0;
        while (difference >= this.nepaliYearDays(index)) {
            this.nepaliYear++;
            difference = difference - this.nepaliYearDays(index);
            index++;
        }

        //Getting nepali month until the difference remains less than 31
        int i = 0;
        while (difference >= this.nepaliMonths[index][i]) {
            difference = difference - this.nepaliMonths[index][i];
            this.nepaliMonth++;
            i++;
        }

        //Remaning days is the date;
        this.nepaliDate = this.nepaliDate + difference;

        this.getDay();

    }

    public String toEnglishString() {
        return toEnglishString("-");
    }

    public String toEnglishString(String format) {
        return this.englishYear + format + grabCorrectNumbering(this.englishMonth) + format + grabCorrectNumbering(this.englishDate);
    }

    public int getEnglishDateDifference(int year, int month, int date) {

        //Getting difference from the current date with the date provided
        int difference = countTotalEnglishDays(this.englishYear, this.englishMonth, this.englishDate) - countTotalEnglishDays(year, month, date);
        return (difference < 0 ? -difference : difference);

    }

    private int countTotalEnglishDays(int year, int month, int date) {

        int totalDays = year * 365 + date;

        for (int i = 0; i < month - 1; i++)
            totalDays = totalDays + this.englishMonths[i];

        totalDays = totalDays + countleap(year, month);
        return totalDays;
    }

    public int countleap(int year, int month) {
        if (month <= 2)
            year--;

        return (int) (Math.floor(year / 4) - Math.floor(year / 100) + Math.floor(year / 400));
    }

    private boolean isEnglishRange(int year, int month, int date) {

        if (year < 1944 || year > 2042)
            return false;

        if (month < 1 || month > 12)
            return false;

        if (date < 1 || date > 31)
            return false;

        return true;

    }

    private boolean isLeapYear(int year) {
        if (year % 4 == 0) {
            return (year % 100 == 0) ? (year % 400 == 0) : true;
        } else
            return false;
    }


    //Nepali to English date conversion
    public void setNepaliDate(int year, int month, int date) throws Exception {
        if (!this.isNepaliRange(year, month, date))
            throw new Exception("Invalid date format.");

        this.nepaliYear = year;
        this.nepaliMonth = month;
        this.nepaliDate = date;

        //Setting english reference to 1944/1/1 with nepali date 2000/9/17
//        this.englishYear = 1944;
//        this.englishMonth = 1;
//        this.englishDate = 1;
        this.englishYear = 1914;
        this.englishMonth = 1;
        this.englishDate = 1;

//        int difference = this.getNepaliDateDifference(2000, 9, 17);
        int difference = this.getNepaliDateDifference(1970, 9, 17);

        //Getting english year untill the difference remains less than 365
        while (difference >= (isLeapYear(this.englishYear) ? 366 : 365)) {
            difference = difference - (isLeapYear(this.englishYear) ? 366 : 365);
            this.englishYear++;
        }

        //Getting english month untill the difference remains less than 31
        int[] monthDays = isLeapYear(this.englishYear) ? this.englishLeapMonths : this.englishMonths;
        int i = 0;
        while (difference >= monthDays[i]) {
            this.englishMonth++;
            difference = difference - monthDays[i];
            i++;
        }

        //Remaining days is the date;
        this.englishDate = this.englishDate + difference;

        this.getDay();

    }

    public String toNepaliString() {
        return toNepaliString("-");
    }

    public String toNepaliString(String format) {
        return this.nepaliYear + format + grabCorrectNumbering(this.nepaliMonth) + format + grabCorrectNumbering(this.nepaliDate);
    }

    private String grabCorrectNumbering(int dateParam) {
        String dateParamString = String.valueOf(dateParam);
        dateParamString = (dateParamString.length() == 1) ? ("0" + dateParamString) : dateParamString;
        return dateParamString;
    }

    public int getNepaliDateDifference(int year, int month, int date) {

        //Getting difference from the current date with the date provided
        int difference = countTotalNepaliDays(this.nepaliYear, this.nepaliMonth, this.nepaliDate) - countTotalNepaliDays(year, month, date);
        return (difference < 0 ? -difference : difference);

    }

    private int countTotalNepaliDays(int year, int month, int date) {
        int total = 0;
        if (year < mimNepaliYear)
            return 0;

        total = total + (date - 1);

        int yearIndex = year - mimNepaliYear;
        for (int i = 0; i < month - 1; i++)
            total = total + this.nepaliMonths[yearIndex][i];

        for (int i = 0; i < yearIndex; i++)
            total = total + this.nepaliYearDays(i);

        return total;
    }

    private int nepaliYearDays(int index) {
        int total = 0;

        for (int i = 0; i < 12; i++)
            total += this.nepaliMonths[index][i];

        return total;
    }

    private boolean isNepaliRange(int year, int month, int date) {
        if (year < mimNepaliYear || year > maxNepalYear)
            return false;

        if (month < 1 || month > 12)
            return false;

        if (date < 1 || date > this.nepaliMonths[year - mimNepaliYear][month - 1])
            return false;

        return true;
    }


    //Class Regular methods

    public int getDay() {

        //Reference date 1943/4/14 Wednesday
        int difference = this.getEnglishDateDifference(1943, 4, 14);
        this.weekDay = ((3 + (difference % 7)) % 7) + 1;
        return this.weekDay;
    }

    public int getEnglishYear() {
        return this.englishYear;
    }

    public int getEnglishMonth() {
        return this.englishMonth;
    }

    public int getEnglishDate() {
        return this.englishDate;
    }

    public int getNepaliYear() {
        return this.nepaliYear;
    }

    public int getNepaliMonth() {
        return this.nepaliMonth;
    }

    public int getNepaliDate() {
        return this.nepaliDate;
    }

    @Override
    public String toString() {
        return "English Date: " + this.toEnglishString() + "\n" + "Nepali Date: " + this.toNepaliString() + "\nDay: " + this.weekDay;
    }

}

