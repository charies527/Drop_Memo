package com.cookandroid.real_memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoList extends AppCompatActivity {
    DBHelper dbHelper;

    Button btnBack3, btnAdd;

    ListView listMemos;

    ArrayList<Memo> memoList;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DBHelper(this);

        btnBack3 = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        listMemos = findViewById(R.id.list_places);

        btnBack3.setOnClickListener(v -> {
            Intent intent = new Intent(MemoList.this, Home.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MemoList.this, AddingMemo.class);
            startActivity(intent);
        });

        memoList = dbHelper.getAllMemos();

        Collections.sort(memoList, (a, b) -> {
                if (a.isFavorite == b.isFavorite) return 0;
                return a.isFavorite ? -1 : 1;
            });

        MemoListAdapter adapter = new MemoListAdapter(this, memoList, dbHelper);
        listMemos.setAdapter(adapter);

        listMemos.setOnItemClickListener((parent, view, position, id) -> {
            Memo memo = memoList.get(position);
        });
    }
}
