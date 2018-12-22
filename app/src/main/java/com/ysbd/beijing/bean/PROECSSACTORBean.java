package com.ysbd.beijing.bean;

import java.io.Serializable;

public class PROECSSACTORBean implements Serializable {

    /**
     * actorsClassify : 0
     * handelStatus : 4
     * personGUID : {BFA7820D-FFFF-FFFF-F16B-BA46000006FA}
     * step : 0
     * updateDate : {"date":4,"day":1,"hours":12,"minutes":7,"month":5,"nanos":0,"seconds":56,"time":1528114076000,"timezoneOffset":0,"year":118}
     */

    private int actorsClassify;
    private int handelStatus;
    private String personGUID;
    private int step;
    private UpdateDateBean updateDate;

    public int getActorsClassify() {
        return actorsClassify;
    }

    public void setActorsClassify(int actorsClassify) {
        this.actorsClassify = actorsClassify;
    }

    public int getHandelStatus() {
        return handelStatus;
    }

    public void setHandelStatus(int handelStatus) {
        this.handelStatus = handelStatus;
    }

    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public UpdateDateBean getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(UpdateDateBean updateDate) {
        this.updateDate = updateDate;
    }

    public static class UpdateDateBean implements Serializable{
        /**
         * date : 4
         * day : 1
         * hours : 12
         * minutes : 7
         * month : 5
         * nanos : 0
         * seconds : 56
         * time : 1528114076000
         * timezoneOffset : 0
         * year : 118
         */

        private int date;
        private int day;
        private int hours;
        private int minutes;
        private int month;
        private int nanos;
        private int seconds;
        private long time;
        private int timezoneOffset;
        private int year;

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getNanos() {
            return nanos;
        }

        public void setNanos(int nanos) {
            this.nanos = nanos;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getTimezoneOffset() {
            return timezoneOffset;
        }

        public void setTimezoneOffset(int timezoneOffset) {
            this.timezoneOffset = timezoneOffset;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
