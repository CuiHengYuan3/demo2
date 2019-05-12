package com.example.lenovo.demo;

public class Data  {
private  String cover;
private Site site;

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getCover() {
        return cover;
    }

    public Site getSite() {
        return site;
    }
}
class Site{
    private  String cat_cn;
    private  String desc;

    public void setCat_cn(String cat_cn) {
        this.cat_cn = cat_cn;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCat_cn() {
        return cat_cn;
    }

    public String getDesc() {
        return desc;
    }
}