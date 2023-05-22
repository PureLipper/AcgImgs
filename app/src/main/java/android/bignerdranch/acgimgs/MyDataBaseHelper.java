package android.bignerdranch.acgimgs;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    /*
     * context：上下文
     * databaseName:创建的数据库名称
     * databaseVersion：数据库版本
     * */
    public MyDataBaseHelper(Context context, String databaseName, int databaseVersion){
        super(context,databaseName,null,databaseVersion);
    }
    /*
     * 数据库第一次创建的时候，调用onCreate；数据库已经创建成功之后，就不调用它了
     * db就是创建的数据库
     * db.execSQL这句是用来创建数据库表
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("create table if not exists MyCollection(" +
                    "id integer primary key autoincrement," +
                    "imgurl String," +
                    "title String," +
                    "author String," +
                    "pid String," +
                    "r18 inteter," +
                    "tags String," +
                    "source integer," +
                    "filename String)");//执行创建表的sql语句
            System.out.println("数据库创建成功");
        }catch (SQLException e){
            System.out.println("数据库初始化失败");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE MyCollection ADD COLUMN source");
    }
}