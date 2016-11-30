package com.example.administrator.myproject.bean;

/**
 * Created by Administrator on 2016/7/20.
 */
public class SystemMessage {
    public String id;
    public String unread;
    public String icon;
    public String content;
    public String alert_content;
    public long ctime;
    public String total;
    public String page;
    public String pagesize;
    public boolean isTheSame(SystemMessage message){
        if (message == null){
            return false;
        }
        return id.equals(message.id);
    }
}
