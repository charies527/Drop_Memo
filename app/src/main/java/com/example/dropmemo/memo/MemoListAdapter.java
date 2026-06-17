package com.example.dropmemo.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dropmemo.R;

import java.util.ArrayList;

public class MemoListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Memo> memoList;
    private final DBHelper dbHelper;

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
        btnFavorite.setImageResource(memo.isFavorite ? R.drawable.ic_favorite : R.drawable.ic_un_favorite);

        btnFavorite.setOnClickListener(v -> {
            memo.isFavorite = !memo.isFavorite;
            dbHelper.updateFavorite(memo.id, memo.isFavorite);
            memoList.sort((a, b) -> {
                if (a.isFavorite != b.isFavorite) {
                    return a.isFavorite ? -1 : 1;
                }
                return Long.compare(b.updatedAt, a.updatedAt);
            });
            notifyDataSetChanged();
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("삭제 확인")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("예", (d, w) -> {
                    dbHelper.deleteMemo(memo.id);
                    memoList.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("아니오", null)
                .show());

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddActivity.class);
            intent.putExtra("id", memo.id);
            intent.putExtra("place", memo.place);
            intent.putExtra("content", memo.content);
            intent.putExtra("isAlarm", memo.isAlarm);
            if (memo.latitude != null && memo.longitude != null) {
                intent.putExtra("latitude", memo.latitude);
                intent.putExtra("longitude", memo.longitude);
            }
            context.startActivity(intent);
        });

        return view;
    }
}
