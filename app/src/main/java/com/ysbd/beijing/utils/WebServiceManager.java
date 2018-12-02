package com.ysbd.beijing.utils;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.Log;

import com.ysbd.beijing.App;
import com.ysbd.beijing.utils.ksoap.MyGBKSe;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by lcjing on 2018/8/29.
 */

public class WebServiceManager {

    private static WebServiceManager instance;

    private boolean debug = false;
    private static final String NAME_SPACE = "http://www.freshpower.com.cn";
    public static final String TAG = "WebServiceManager";

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private String userName;

    private WebServiceManager() {
        userName = App.getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE)
                .getString(Constants.USER_NAME, "");
    }

    public static WebServiceManager getInstance() {
        if (instance == null) {
            instance = new WebServiceManager();
        }
        return instance;
    }

    public String connect(String method, String url, String json) {
        int envolopeVersion = SoapEnvelope.VER11;
        SoapObject request = new SoapObject(NAME_SPACE, method);
        request.addProperty("in0", json);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        MyGBKSe se = new MyGBKSe(url);
        log("****url************" + method + "***********url****\n" + url);
        log(userName + "****json************" + method + "***********json****\n" + json);
//        ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        //添加ClipData对象到剪切板中
        String log = "json=" + json + ";   url=" + url + ";  method=" + method;
//        clipboardManager.setText(log);
//        String soapAction = "http://mobile.risesoft.net/login";
        try {
            String a = "";
            se.call(null, envelope);
            if (envelope.bodyIn instanceof SoapObject) {
                SoapObject response1 = (SoapObject) envelope.bodyIn;
                Object object = response1.getProperty(0);
                if (response1.getProperty(0) instanceof SoapPrimitive) {
                    SoapPrimitive o = (SoapPrimitive) response1.getProperty(0);
                    a = o.getValue().toString();
                    log(userName + "****json************" + method + "***********json****\n" + json);
                    log("****resultData************" + method + "***********resultData****\n" + a);
                }

            } else {
                a = "";
                log("****error************不是SoapObject实例***********error****\n" + envelope.bodyIn.toString());
                log("****error************" + method + "***********error****\n" + envelope.bodyIn.toString());
            }
            return a;
        } catch (ClassCastException e) {
            log("****Exception************类转换异常异常***********Exception****\n" + e.getMessage());
        } catch (XmlPullParserException e) {
            log("****Exception************xml异常***********Exception****\n" + e.getMessage());
        } catch (IOException e) {
//            e.printStackTrace();
            log("****Exception************IO异常***********Exception****\n" + e.getMessage());
            log("****Exception************" + method + "***********Exception****\n" + e.getMessage());
        }
        return "";
    }



//    public static String questToWebService(String wsdl_url,String name_sapce,String methodName) {
//        String result = "";
//        //（1）创建HttpTransportSE对象，该对象用于调用WebService操作
//        HttpTransportSE httpTransportSE = new HttpTransportSE(wsdl_url,90*1000);
//        //（2）创建SoapSerializationEnvelope对象
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
//        //（3）创建SoapObject对象，创建该对象时需要传入所要调用的Web Service的命名空间和WebService方法名
//        SoapObject request = new SoapObject(name_sapce, methodName);
//        //(4) //填入需要传入的参数
//
//        //（5）调用SoapSerializationEnvelope的setOutputSoapObject()方法，或者直接对bodyOut属性赋值，
//        //将前两步创建的SoapObject对象设为SoapSerializationEnvelope的传出SOAP消息体
//        envelope.bodyOut = request;
////        envelope.setOutputSoapObject(request);
//        try {
//            //（6）调用对象的call()方法，并以SoapSerializationEnvelope作为参数调用远程的web service
//            httpTransportSE.call(methodName, envelope);//调用
//            if (envelope.getResponse() != null) {
//                result = envelope.getResponse().toString().trim();
//            }
//            return result;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }
//        //解析该对象，即可获得调用web service的返回值
//
//        return result;
//    }


    public String connect(final String method, final String url) {

        int envolopeVersion = SoapEnvelope.VER11;
        SoapObject request = new SoapObject(NAME_SPACE, method);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
        envelope.bodyOut=request;
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        MyGBKSe se = new MyGBKSe(url);
        try {
            String a = "";
            se.call(null, envelope);
            if (envelope.bodyIn instanceof SoapObject) {
                SoapObject response1 = (SoapObject) envelope.bodyIn;
                if (response1.getProperty(0) instanceof SoapPrimitive) {
                    SoapPrimitive o = (SoapPrimitive) response1.getProperty(0);
                    a = o.getValue().toString();
                }
            } else {
                a = "";
            }
            return a;
        } catch (ClassCastException e) {
            log("****Exception************类转换异常异常***********Exception****\n" + e.getMessage());
        } catch (XmlPullParserException e) {
            log("****Exception************xml异常***********Exception****\n" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log("****Exception************IO异常***********Exception****\n" + e.getMessage());
            log("****Exception************" + method + "***********Exception****\n" + e.getMessage());
        }
        return "";
    }




    public String connect(String method, String url, String json, ClipboardManager clipboardManager) {
        int envolopeVersion = SoapEnvelope.VER11;
        SoapObject request = new SoapObject(NAME_SPACE, method);
        request.addProperty("in0", json);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        MyGBKSe se = new MyGBKSe(url);
        try {
            String a = "";
            se.call(null, envelope);
            if (envelope.bodyIn instanceof SoapObject) {
                SoapObject response1 = (SoapObject) envelope.bodyIn;
                Object object = response1.getProperty(0);
                if (response1.getProperty(0) instanceof SoapPrimitive) {
                    SoapPrimitive o = (SoapPrimitive) response1.getProperty(0);
                    a = o.getValue().toString();
                    log(userName + "****json************" + method + "***********json****\n" + json);
                    log("****resultData************" + method + "***********resultData****\n" + a);
                }

            } else {
                a = "";
                log("****error************" + method + "***********error****\n" + envelope.bodyIn.toString());
            }
            return a;
        } catch (IOException | XmlPullParserException | ClassCastException e) {
//            e.printStackTrace();
            log("****Exception************" + method + "***********Exception****\n" + e.getMessage());
        }
        return "";
    }

    private void log(String msg) {
        if (debug) {
            //信息太长,分段打印
            //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
            //  把4*1024的MAX字节打印长度改为2001字符数
            int max_str_length = 2001 - TAG.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.d(TAG, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.d(TAG, msg);
//            Log.d(TAG,msg);
        }

    }
}
