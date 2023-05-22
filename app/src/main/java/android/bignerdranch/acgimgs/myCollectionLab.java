package android.bignerdranch.acgimgs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class myCollectionLab {
    private static myCollectionLab sMyCollectionLab;
    private ArrayList<myCollection> mMyCollections;
    public static myCollectionLab get(Context context){
        if(sMyCollectionLab == null){
            sMyCollectionLab = new myCollectionLab(context);
        }
        return  sMyCollectionLab;
    }

    private myCollectionLab(Context context) {
        mMyCollections = new ArrayList<>();
        SQLiteDatabase db = MainActivity.getMyDataBaseHelper().getWritableDatabase();
        Cursor cursor = db.query("MyCollection",null,null,null,null,null,null);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            myCollection collection = null;
            int source = cursor.getInt(cursor.getColumnIndexOrThrow("source"));
            String url = "";
            String id = "";
            String filename = "";
            switch (source){
                case 0:
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    url = cursor.getString(cursor.getColumnIndexOrThrow("imgurl"));
                    filename = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
                    collection = new myCollection(url,id,source,filename);
                    break;
                case 1:
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String pid = cursor.getString(cursor.getColumnIndexOrThrow("pid"));
                    url = cursor.getString(cursor.getColumnIndexOrThrow("imgurl"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                    int r18 = cursor.getInt(cursor.getColumnIndexOrThrow("r18"));
                    String tags = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                    filename = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
                    collection = new myCollection(url,id,title,author,tags,pid,r18,source,filename);
                    break;
            }
            mMyCollections.add(collection);
        }
        cursor.close();
    }

    public ArrayList<myCollection> getMyCollections(){
        return mMyCollections;
    }
}
