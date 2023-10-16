package android.bignerdranch.acgimgs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class collectionLabActivity extends AppCompatActivity {
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
        myCollectionLab collectionLab = myCollectionLab.get(collectionLabActivity.this);
        ArrayList<myCollection> collections = collectionLab.getMyCollections();
        if(newCollections != null){
            collections.addAll(newCollections);
            newCollections = null;
        }
        mAdapter = new CollectionAdapter(collections);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class CollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        String id;
        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(collectionLabActivity.this);
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
            Intent intent = new Intent(collectionLabActivity.this,collectionCheckActivity.class);
            intent.putExtra("id",id);
            startActivity(intent);
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
            id = myCollection.getId();
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
            LayoutInflater layoutInflater = LayoutInflater.from(collectionLabActivity.this);
            return new CollectionHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            myCollection collection = mMyCollections.get(position);
            ((CollectionHolder) holder).bind(collection);
            File file = new File(getExternalFilesDir("imageCache").getAbsolutePath() + "/" + collection.getFilename());
            Glide.with(collectionLabActivity.this)
                    .load(file)
                    .into(((CollectionHolder) holder).mImage);
        }

        @Override
        public int getItemCount() {
            return mMyCollections.size();
        }
    }

}