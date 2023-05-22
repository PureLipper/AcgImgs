package android.bignerdranch.acgimgs;

import static java.lang.System.out;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class collection extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CollectionAdapter mAdapter;
    private Button back;
    private SQLiteDatabase db;
    private ArrayList<myCollection> newCollections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_collection);

        Intent intent = getIntent();
        if(intent.getParcelableArrayListExtra("newCollections") != null){
            newCollections = intent.getParcelableArrayListExtra("newCollections");
        }


        db = MainActivity.getMyDataBaseHelper().getWritableDatabase();
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        update();

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("opened",true);
    }

    private void update() {
        myCollectionLab collectionLab = myCollectionLab.get(collection.this);
        ArrayList<myCollection> collections = collectionLab.getMyCollections();
        if(newCollections != null){
            collections.addAll(newCollections);
            newCollections = null;
        }
        mAdapter = new CollectionAdapter(collections);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class CollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        String filename;
        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(collection.this);
            builder.setTitle("确认" ) ;
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage("是否删除此收藏？" ) ;
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.delete("MyCollection","id=?",new String[]{mMyCollection.getId()});
                    mAdapter.mMyCollections.remove(mMyCollection);
                    mAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("否", null);
            builder.show();
            return true;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("filename",filename);
            collection.this.setResult(RESULT_OK,intent);
            collection.this.finish();
        }

        private myCollection mMyCollection;
        private ImageView mImage;
        private TextView mTextView;

        public CollectionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.collection_piece, parent, false));

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.Img_button);
            mTextView = (TextView) itemView.findViewById(R.id.detail);
        }

        public void bind(myCollection myCollection) {
            mMyCollection = myCollection;
            filename = myCollection.getFilename();
            mTextView.setText(myCollection.toString());
        }
    }
    private class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<myCollection> mMyCollections;

        public CollectionAdapter(List<myCollection> collections) {
            mMyCollections = collections;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(collection.this);
            return new CollectionHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            myCollection collection = mMyCollections.get(position);
            ((CollectionHolder) holder).bind(collection);
            File file = new File(getExternalFilesDir("imageCache").getAbsolutePath() + "/" + collection.getFilename());
            Glide.with(collection.this)
                    .load(file)
                    .into(((CollectionHolder) holder).mImage);
        }

        @Override
        public int getItemCount() {
            return mMyCollections.size();
        }
    }

}