package com.cookandroid.real_memo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MemoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Memo> memoList;
    private DBHelper dbHelper;

    public MemoListAdapter(Context context, ArrayList<Memo> memoList, DBHelper dbHelper) {
        this.context = context;
        this.memoList = memoList;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return memoList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_memo, parent, false);
        }

        TextView txtPlace = view.findViewById(R.id.txtPlace);
        TextView txtContent = view.findViewById(R.id.txtContent);
        ImageButton btnFavorite = view.findViewById(R.id.btnFavorite);
        ImageButton btnDelete = view.findViewById(R.id.btnDelete);

        Memo memo = memoList.get(position);

        txtPlace.setText(memo.place);
        txtContent.setText(memo.content);

        if (memo.isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        btnFavorite.setOnClickListener(v -> {
            memo.isFavorite = !memo.isFavorite;
            dbHelper.updateFavorite(memo.id, memo.isFavorite);
            notifyDataSetChanged();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("삭제 확인")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("네", (d, w) -> {
                        dbHelper.deleteMemo(memo.id);
                        memoList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        });

        return view;
    }
}