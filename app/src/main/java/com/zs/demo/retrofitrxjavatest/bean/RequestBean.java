package com.zs.demo.retrofitrxjavatest.bean;


import com.zs.demo.retrofitrxjavatest.request.RequestBaseParams;
import com.zs.demo.retrofitrxjavatest.util.NewAES;

/**
 * Created by zs
 * Date：2017年 09月 21日
 * Time：16:43
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */

public class RequestBean {

    private String version;
    private String optioncode;
    private String signature;
    private String timestamp;
    private String nonce;
    private String option;

    public RequestBean(String optioncode, String option) {

        this.optioncode = optioncode;
        this.option = NewAES.encrypt(option,RequestBaseParams.IMEncodingAESKey);

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOptioncode() {
        return optioncode;
    }

    public void setOptioncode(String optioncode) {
        this.optioncode = optioncode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
