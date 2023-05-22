package android.bignerdranch.acgimgs;

import java.util.Arrays;

public class Imgs_source1 {
    private String pid;
    private int p;
    private String uid;
    private String title;
    private String author;
    private int r18;
    private int width;
    private int height;
    private String[] tags;
    private String url;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getR18() {
        return r18;
    }

    public void setR18(int r18) {
        this.r18 = r18;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Imgs{" +
                "pid='" + pid + '\'' +
                ", p=" + p +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", r18=" + r18 +
                ", width=" + width +
                ", height=" + height +
                ", tags=" + Arrays.toString(tags) +
                ", url='" + url + '\'' +
                '}';
    }
}
