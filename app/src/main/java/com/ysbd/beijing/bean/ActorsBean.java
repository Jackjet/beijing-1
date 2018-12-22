package com.ysbd.beijing.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lcjing on 2018/8/30.
 */

public class ActorsBean implements Serializable {


    @SerializedName("RECIPIENTSACTORS")
    private List<RECIPIENTSACTORSBean> recipientsactorsBeans;
    @SerializedName("INFO")
    private InfoBean infoBean;
    @SerializedName("PROECSSACTOR")
    private PROECSSACTORBean proecssactorBean;

    public List<RECIPIENTSACTORSBean> getRecipientsactorsBeans() {
        return recipientsactorsBeans;
    }

    public void setRecipientsactorsBeans(List<RECIPIENTSACTORSBean> recipientsactorsBeans) {
        this.recipientsactorsBeans = recipientsactorsBeans;
    }

    public InfoBean getInfoBean() {
        return infoBean;
    }

    public void setInfoBean(InfoBean infoBean) {
        this.infoBean = infoBean;
    }

    public PROECSSACTORBean getProecssactorBean() {
        return proecssactorBean;
    }

    public void setProecssactorBean(PROECSSACTORBean proecssactorBean) {
        this.proecssactorBean = proecssactorBean;
    }
}
