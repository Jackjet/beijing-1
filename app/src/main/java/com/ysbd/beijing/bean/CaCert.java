package com.ysbd.beijing.bean;

import java.io.Serializable;
import java.util.List;

public class CaCert implements Serializable {

    /**
     * userinfo : [{"EMPLOYEE_LOGINNAME":"刘尧xc","PADPASSWORD":"123456","USERID":"{BFA8006E-0000-0000-7877-C03F00000003}","EMPLOYEE_JOBTITLES":null,"DEPARTMENT_GUID":"{0A2FCA25-FFFF-FFFF-9438-791800000001}","EMPLOYEE_MSSPID":"af4b248012df6d1ee2ec705ea50a3ece6acc8a9781c4757f7e608e99da0edc39"},{"EMPLOYEE_LOGINNAME":"刘尧","PADPASSWORD":"872BE7378D2E5C4B747F2547144C6DC5","USERID":"{0A2FCA25-FFFF-FFFF-AB8C-146500000001}","EMPLOYEE_JOBTITLES":"主任科员","DEPARTMENT_GUID":"{BFA7820D-FFFF-FFFF-F16C-486100000011}","EMPLOYEE_MSSPID":"af4b248012df6d1ee2ec705ea50a3ece6acc8a9781c4757f7e608e99da0edc39"}]
     * success : true
     */

    private String success;
    private List<UserinfoBean> userinfo;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<UserinfoBean> getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(List<UserinfoBean> userinfo) {
        this.userinfo = userinfo;
    }

    public static class UserinfoBean {
        /**
         * EMPLOYEE_LOGINNAME : 刘尧xc
         * PADPASSWORD : 123456
         * USERID : {BFA8006E-0000-0000-7877-C03F00000003}
         * EMPLOYEE_JOBTITLES : null
         * DEPARTMENT_GUID : {0A2FCA25-FFFF-FFFF-9438-791800000001}
         * EMPLOYEE_MSSPID : af4b248012df6d1ee2ec705ea50a3ece6acc8a9781c4757f7e608e99da0edc39
         */

        private String EMPLOYEE_LOGINNAME;
        private String PADPASSWORD;
        private String USERID;
        private Object EMPLOYEE_JOBTITLES;
        private String DEPARTMENT_GUID;
        private String EMPLOYEE_MSSPID;

        public String getEMPLOYEE_LOGINNAME() {
            return EMPLOYEE_LOGINNAME;
        }

        public void setEMPLOYEE_LOGINNAME(String EMPLOYEE_LOGINNAME) {
            this.EMPLOYEE_LOGINNAME = EMPLOYEE_LOGINNAME;
        }

        public String getPADPASSWORD() {
            return PADPASSWORD;
        }

        public void setPADPASSWORD(String PADPASSWORD) {
            this.PADPASSWORD = PADPASSWORD;
        }

        public String getUSERID() {
            return USERID;
        }

        public void setUSERID(String USERID) {
            this.USERID = USERID;
        }

    }
}
