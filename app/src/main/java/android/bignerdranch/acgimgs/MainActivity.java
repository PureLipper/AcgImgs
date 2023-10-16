package android.bignerdranch.acgimgs;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static MyDataBaseHelper myDataBaseHelper;
    private SharedPreferences mSharedPreferences;

    public static MyDataBaseHelper getMyDataBaseHelper() {
        return myDataBaseHelper;
    }

    String DataBase_Name = "MyCollections";
    String Table_Name = "MyCollection";
    private Boolean opened = false;
    private ImageView ImageView;
    private Bitmap imgBitmap = null;
    private ImageButton refresh;
    private ImageButton collect;
    private ImageButton menu;
    private ImageButton download;
    private ImageButton search_button;
    private EditText search_EditText;
    private Button retry;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private String imgUrl;
    private String title;
    private String pid;
    private String author;
    private String tags;
    private int r18;

    private String search_text;


    private ArrayList<myCollection> new_collections = new ArrayList<>();
    private myCollection new_collection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);

        if (!isNetworkConnected(MainActivity.this)) {
            Toast.makeText(this, "请检查您的网络连接情况", Toast.LENGTH_SHORT).show();
        }

        myDataBaseHelper = new MyDataBaseHelper(MainActivity.this, DataBase_Name, 3);

        ImageView = (ImageView) findViewById(R.id.Img_view);
        menu = (ImageButton) findViewById(R.id.menu);
        download = (ImageButton) findViewById(R.id.download);
        refresh = (ImageButton) findViewById(R.id.refresh);
        collect = (ImageButton) findViewById(R.id.collect);
        search_button = (ImageButton) findViewById(R.id.search_button);
        search_EditText = (EditText) findViewById(R.id.search_text);
        retry = (Button) findViewById(R.id.retry);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.menu_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            imgUrl = savedInstanceState.getString("url");
            title = savedInstanceState.getString("title");
            pid = savedInstanceState.getString("pid");
            author = savedInstanceState.getString("author");
            tags = savedInstanceState.getString("tags");
            r18 = savedInstanceState.getInt("r18");
            new_collections = savedInstanceState.getParcelableArrayList("newCollections");
            opened = savedInstanceState.getBoolean("opened");
            search_text = savedInstanceState.getString("search_text");
            search_EditText.setText(search_text);
            requestWebPhotoBitmap(imgUrl);
            if (checkIfCollected(imgUrl)) {
                collect.setImageResource(R.drawable.collected);
            }
            savedInstanceState.clear();
        } else {
            initData();
        }

        search_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search_text = s.toString();
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.myCollection):
                        Intent intent = new Intent(MainActivity.this, collectionLabActivity.class);
                        if (new_collections != null && opened) {
                            intent.putParcelableArrayListExtra("newCollections", new_collections);
                        }
                        startActivityForResult(intent, 1);
                        new_collections.clear();
                        opened = true;
                        break;
                    case (R.id.settings):
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                }
                return true;
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.setVisibility(View.INVISIBLE);
                ObjectAnimator.ofFloat(refresh,"rotation",0,360).start();
                initData();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = System.currentTimeMillis() + ".jpg";
                int myWidth = Target.SIZE_ORIGINAL;
                int myHeight = Target.SIZE_ORIGINAL;
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(imgUrl)
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveBitmap(resource, getExternalFilesDir("imageCache").getAbsolutePath() + "/" + filename);
                            }
                        });

                if (!checkIfCollected(imgUrl)) {
                    String source = mSharedPreferences.getString("source", "0");
                    switch (source) {
                        case "0": {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("imgurl", imgUrl);
                            contentValues.put("source", 0);
                            contentValues.put("filename", filename);
                            SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                            long id = db.insert(Table_Name, null, contentValues);
                            if (id != -1) {
                                Toast.makeText(MainActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                new_collection = new myCollection(imgUrl, String.valueOf(id), 0, filename);
                                collect.setImageResource(R.drawable.collected);
                            } else
                                Toast.makeText(MainActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "1": {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("imgurl", imgUrl);
                            contentValues.put("r18", r18);
                            contentValues.put("title", title);
                            contentValues.put("author", author);
                            contentValues.put("pid", pid);
                            contentValues.put("tags", tags);
                            contentValues.put("source", 1);
                            contentValues.put("filename", filename);
                            SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                            long id = db.insert(Table_Name, null, contentValues);
                            if (id != -1) {
                                Toast.makeText(MainActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                new_collection = new myCollection(imgUrl, String.valueOf(id), title, author, tags, pid, r18, 1, filename);
                                collect.setImageResource(R.drawable.collected);
                            } else
                                Toast.makeText(MainActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    new_collections.add(new_collection);
                } else Toast.makeText(MainActivity.this, "收藏中已有此图片", Toast.LENGTH_SHORT).show();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SaveJpg(ImageView)) {
                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_EditText.clearFocus();
                initData();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDataBaseHelper != null)
            myDataBaseHelper.close();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", imgUrl);
        outState.putString("title", title);
        outState.putString("pid", pid);
        outState.putString("author", author);
        outState.putInt("r18", r18);
        outState.putString("tags", tags);
        outState.putParcelableArrayList("newCollections", new_collections);
        outState.putBoolean("opened", opened);
        outState.putString("search_text",search_text);
    }

    private void initData() {
        ImageView.setImageResource(R.drawable.loading);
        collect.setImageResource(R.drawable.collect);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://image.anosu.top/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Img_Interface img_interface = retrofit.create(Img_Interface.class);

        String source = mSharedPreferences.getString("source", "0");
        switch (source) {
            case "0": {
                Call<Imgs_source0> call = img_interface.getImg(get_sub_url());
                call.enqueue(new Callback<Imgs_source0>() {
                    @Override
                    public void onResponse(Call<Imgs_source0> call, Response<Imgs_source0> response) {
                        Log.i("onResponse", response.body().toString());
                        imgUrl = response.body().getImgurl();
                        requestWebPhotoBitmap(imgUrl);
                        if (checkIfCollected(imgUrl)) {
                            collect.setImageResource(R.drawable.collected);
                        }
                    }

                    @Override
                    public void onFailure(Call<Imgs_source0> call, Throwable t) {
                        Log.i("onFailure", "请求失败");
                    }
                });
                break;
            }
            case "1": {
                Call<List<Imgs_source1>> call = img_interface.getImgs(get_sub_url());
                call.enqueue(new Callback<List<Imgs_source1>>() {
                    @Override
                    public void onResponse(Call<List<Imgs_source1>> call, Response<List<Imgs_source1>> response) {
                        if (response.body().size() != 0) {
                            Log.i("onResponse", response.body().toString());
                            imgUrl = response.body().get(0).getUrl();
                            title = response.body().get(0).getTitle();
                            pid = response.body().get(0).getPid();
                            author = response.body().get(0).getAuthor();
                            r18 = response.body().get(0).getR18();
                            tags = Arrays.toString(response.body().get(0).getTags());
                            requestWebPhotoBitmap(imgUrl);
                            if (checkIfCollected(imgUrl)) {
                                collect.setImageResource(R.drawable.collected);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "找不到该关键词", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Imgs_source1>> call, Throwable t) {
                        Log.i("onFailure", "请求失败");
                    }
                });
                break;
            }
        }

    }

    /**
     * 获取 网络图片 Bitmap
     *
     * @param imgUrl 网络图片url，或本地路径
     */
    private void requestWebPhotoBitmap(String imgUrl) {
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(imgUrl)
                .listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestWebPhotoBitmap(imgUrl);
                        retry.setVisibility(View.INVISIBLE);
                    }
                });
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
                })
            .into(new CustomTarget<Bitmap>() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    imgBitmap = resource;
                    ImageView.setImageBitmap(imgBitmap);
                }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public static boolean SaveJpg(ImageView view) {

        try {
            Drawable drawable = view.getDrawable();
            if (drawable == null) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            Uri dataUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri fileUri = view.getContext().getContentResolver().insert(dataUri, values);

            // 如果保存不成功，insert没有任何错误信息，此时调用update会有错误信息提示
//            view.getContext().getContentResolver().update(dataUri, values, "", null);

            if (fileUri == null) {
                return false;
            }

            OutputStream outStream = view.getContext().getContentResolver().openOutputStream(fileUri);

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            // 刷新相册
            /*
            String[] files = new String[1];
            String[] mimeTypes = new String[1];
            files[0] = filePath;
            mimeTypes[0] = "image/jpeg";
            MediaScannerConnection.scanFile(view.getContext(), files, mimeTypes, null);
            */
            view.getContext().sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", fileUri));
            return true;

        } catch (IOException ignored) {
        }
        return false;
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean checkIfCollected(String checkUlr) {
        Cursor cursor = MainActivity.getMyDataBaseHelper().getWritableDatabase()
                .query("MyCollection", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow("imgurl"));
                if (url.equals(checkUlr)) {
                    return true;
                }
            }
        }
        cursor.close();
        return false;
    }

    private String get_sub_url() {
        String sub_url = "pixiv/json";
        String mode = mSharedPreferences.getString("mode", "0");
        String source = mSharedPreferences.getString("source", "1");
        if (Objects.equals(source, "0")) {
            sub_url = "https://api.vvhan.com/api/acgimg?type=json";
        } else if (Objects.equals(source, "1")) {
            if (Objects.equals(mode, "0")) {
                sub_url += "?r18=0";
            } else if (Objects.equals(mode, "1")) {
                sub_url += "?r18=1";
            } else {
                sub_url += "?r18=2";
            }
            if (search_text != null) {
                sub_url += "&keyword=" + search_text;
            }
        }
        return sub_url;
    }

    public void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }

        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(filePic.getPath()))));
    }
}