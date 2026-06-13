package com.example.dropmemo.memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import com.example.dropmemo.R;

public class ListActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Button btnBack, btnAdd;
    ListView listMemos;
    ArrayList<Memo> memoList;
    MemoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DBHelper(this);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        listMemos = findViewById(R.id.list_places);

        btnBack.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, AddActivity.class);
            startActivity(intent);
        });

        memoList = new ArrayList<>();

        adapter = new MemoListAdapter(this, memoList, dbHelper);

        listMemos.setAdapter(adapter);

        loadMemos();

        listMemos.setOnItemClickListener((parent, view, position, id) -> {
            Memo memo = memoList.get(position);
        });
    }
    private void loadMemos() {

        memoList.clear();

        memoList.addAll(dbHelper.getAllMemos());

        Collections.sort(memoList, (a, b) -> {
            if (a.isFavorite != b.isFavorite) {
                return a.isFavorite ? -1 : 1;
            }
            return Long.compare(b.updatedAt, a.updatedAt);
        });

        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();

        loadMemos();
    }
}