package com.ysbd.beijing.bean;

import java.io.Serializable;

public class RECIPIENTSACTORSBean implements Serializable {


    /**
     * RECIPIENTSACTORSINFO : 王婴(北京市财政局,局领导),step=0,actorsClassify=0,handelStatus=0
     * RECIPIENTSACTORSVALUE : {"actorsClassify":0,"handelStatus":0,"personGUID":"{0A2FCA25-FFFF-FFFF-81CF-8A1700000001}","step":0,"updateDate":null}
     */

    private String RECIPIENTSACTORSINFO;
    private RECIPIENTSACTORSVALUEBean RECIPIENTSACTORSVALUE;

    public String getRECIPIENTSACTORSINFO() {
        return RECIPIENTSACTORSINFO;
    }

    public void setRECIPIENTSACTORSINFO(String RECIPIENTSACTORSINFO) {
        this.RECIPIENTSACTORSINFO = RECIPIENTSACTORSINFO;
    }

    public RECIPIENTSACTORSVALUEBean getRECIPIENTSACTORSVALUE() {
        return RECIPIENTSACTORSVALUE;
    }

    public void setRECIPIENTSACTORSVALUE(RECIPIENTSACTORSVALUEBean RECIPIENTSACTORSVALUE) {
        this.RECIPIENTSACTORSVALUE = RECIPIENTSACTORSVALUE;
    }

    public static class RECIPIENTSACTORSVALUEBean implements Serializable{
        /**
         * actorsClassify : 0
         * handelStatus : 0
         * personGUID : {0A2FCA25-FFFF-FFFF-81CF-8A1700000001}
         * step : 0
         * updateDate : null
         */

        private int actorsClassify;
        private int handelStatus;
        private String personGUID;
        private int step;
        private Object updateDate;

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

        public Object getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Object updateDate) {
            this.updateDate = updateDate;
        }
    }
}
