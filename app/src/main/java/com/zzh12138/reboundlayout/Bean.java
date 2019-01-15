package com.zzh12138.reboundlayout;

/**
 * Created by zhangzhihao on 2019/1/8 16:43.
 */
public class Bean {
    private String title;
    private String url;

    public Bean(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
