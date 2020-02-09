/**
 * @file HistoryListAdapter.java
 * @brief 撮影履歴リストに情報を送るadapter
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/02/09
 */

package com.hyungilee.objectdetectioncameraapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyungilee.objectdetectioncameraapp.R;

import java.io.File;

import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry.COL_HISTORY_DATE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry.COL_HISTORY_ITEM;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry.COL_HISTORY_PIC;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.savePath;

/**
 * @brief 撮影履歴リストに情報を送るadapter
 *
 * @par 概要
 * adapterとして、historylist_itemのリストフォーマットに
 * 写真、写真の日付け、検査項目を別々に送ってくれるadapter
 */
public class HistoryListAdapter extends CursorAdapter {

    @SuppressWarnings("deprecation")
    public HistoryListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 写真名と保存されているpath
        ImageView detectPic = (ImageView)view.findViewById(R.id.detectPic);
        String picture_name = cursor.getString(cursor.getColumnIndex(COL_HISTORY_PIC));
        // 写真のpathと名前で検索、そのファイルをImageViewで表示
        File imgFile = new File(savePath, picture_name);
        if(imgFile.exists()){
            Bitmap pictureBitmap= BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            detectPic.setImageBitmap(pictureBitmap);
        // pathにイメージファイルがない場合
        } else{
            Bitmap noImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.noimage);
            detectPic.setImageBitmap(noImage);
        }
        // 撮影日時をTextViewで見せてくれる
        TextView detectPicDate = (TextView)view.findViewById(R.id.detectPicDate);
        String date = cursor.getString(cursor.getColumnIndex(COL_HISTORY_DATE));
        detectPicDate.setText(date);
        // 検査項目をTextViewで見せてくれる
        TextView detectPicName = (TextView)view.findViewById(R.id.detectPicName);
        String inspection_item = cursor.getString(cursor.getColumnIndex(COL_HISTORY_ITEM));
        detectPicName.setText(inspection_item);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //xmlファイルをadapterでviewに付ける
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.historylist_item, parent, false);
        return view;
    }

}