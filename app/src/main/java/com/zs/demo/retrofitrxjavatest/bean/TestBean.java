package com.zs.demo.retrofitrxjavatest.bean;

/**
 * Created by zs
 * Date：2017年 09月 25日
 * Time：10:28
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */

public class TestBean extends BaseResponse {

    private String backgroundpic;

    private String begintime;

    public String getBackgroundpic() {
        return backgroundpic;
    }

    public void setBackgroundpic(String backgroundpic) {
        this.backgroundpic = backgroundpic;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }
}
