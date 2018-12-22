package com.ysbd.beijing.bean;

import java.io.Serializable;

public class InfoBean implements Serializable {

    /**
     * actionName : 请主管局长批示
     * currentStep : 5
     * departName : 北京市财政局,局领导
     * preActionGUID : {E5078A30-A791-43F9-9972-54BB5F511C55}
     * proecssActor : {"actorsClassify":0,"handelStatus":4,"personGUID":"{0A2FCA25-FFFF-FFFF-81CF-8A1700000001}","step":0,"updateDate":{"date":5,"day":2,"hours":16,"minutes":30,"month":5,"nanos":0,"seconds":18,"time":1528216218000,"timezoneOffset":0,"year":118}}
     * recipientsActors : {}
     * taskName : 主管局长批示
     * updateDate : {"date":5,"day":2,"hours":16,"minutes":30,"month":5,"nanos":0,"seconds":18,"time":1528216218000,"timezoneOffset":0,"year":118}
     * updateDateString : 2018-06-05 16:30
     */

    private String actionName;
    private int currentStep;
    private String departName;
    private String preActionGUID;
    private ProecssActorBean proecssActor;
    private RecipientsActorsBean recipientsActors;
    private String taskName;
    private UpdateDateBeanX updateDate;
    private String updateDateString;

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getPreActionGUID() {
        return preActionGUID;
    }

    public void setPreActionGUID(String preActionGUID) {
        this.preActionGUID = preActionGUID;
    }

    public ProecssActorBean getProecssActor() {
        return proecssActor;
    }

    public void setProecssActor(ProecssActorBean proecssActor) {
        this.proecssActor = proecssActor;
    }

    public RecipientsActorsBean getRecipientsActors() {
        return recipientsActors;
    }

    public void setRecipientsActors(RecipientsActorsBean recipientsActors) {
        this.recipientsActors = recipientsActors;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public UpdateDateBeanX getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(UpdateDateBeanX updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateDateString() {
        return updateDateString;
    }

    public void setUpdateDateString(String updateDateString) {
        this.updateDateString = updateDateString;
    }

    public static class ProecssActorBean implements Serializable{
        /**
         * actorsClassify : 0
         * handelStatus : 4
         * personGUID : {0A2FCA25-FFFF-FFFF-81CF-8A1700000001}
         * step : 0
         * updateDate : {"date":5,"day":2,"hours":16,"minutes":30,"month":5,"nanos":0,"seconds":18,"time":1528216218000,"timezoneOffset":0,"year":118}
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
             * date : 5
             * day : 2
             * hours : 16
             * minutes : 30
             * month : 5
             * nanos : 0
             * seconds : 18
             * time : 1528216218000
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

    public static class RecipientsActorsBean implements Serializable{
    }

    public static class UpdateDateBeanX implements Serializable{
        /**
         * date : 5
         * day : 2
         * hours : 16
         * minutes : 30
         * month : 5
         * nanos : 0
         * seconds : 18
         * time : 1528216218000
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
