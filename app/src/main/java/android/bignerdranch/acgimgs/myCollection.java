package android.bignerdranch.acgimgs;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

public class myCollection implements Parcelable {
    private String filename;
    private int source;
    private String id;
    private Bitmap image;
    private String ImgUrl;
    private String title;
    private String pid;
    private String author;
    private String tags;
    private int r18;

    public myCollection(String imgUrl, String id, String title, String author, String tags, String pid, int r18, int source,String filename) {
        this.source = source;
        this.ImgUrl = imgUrl;
        this.id = id;
        this.author = author;
        this.pid = pid;
        this.tags = tags;
        this.r18 = r18;
        this.title = title;
        this.filename = filename;
    }

    public myCollection(String imgUrl, String id, int source,String filename) {
        this.ImgUrl = imgUrl;
        this.source = source;
        this.id = id;
        this.author = "unknown";
        this.pid = "unknown";
        this.tags = "unknown";
        this.r18 = 0;
        this.title = "unknown";
        this.filename = filename;
    }

    protected myCollection(Parcel in) {
        source = in.readInt();
        id = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        ImgUrl = in.readString();
        title = in.readString();
        pid = in.readString();
        author = in.readString();
        tags = in.readString();
        r18 = in.readInt();
    }

    public static final Creator<myCollection> CREATOR = new Creator<myCollection>() {
        @Override
        public myCollection createFromParcel(Parcel in) {
            return new myCollection(in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),in.readInt(),in.readInt(),in.readString());
        }

        @Override
        public myCollection[] newArray(int size) {
            return new myCollection[size];
        }
    };

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getR18() {
        return r18;
    }

    public void setR18(int r18) {
        this.r18 = r18;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public String toString() {
        return "| 源" + (source + 1) +
                " | 标题：" + title +
                " | p站id：" + pid +
                " | 作者：" + author +
                " | r18：" + r18 +
                " | 标签：" + tags;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ImgUrl);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(tags);
        dest.writeString(pid);
        dest.writeInt(r18);
        dest.writeInt(source);
        dest.writeString(filename);
    }
}
