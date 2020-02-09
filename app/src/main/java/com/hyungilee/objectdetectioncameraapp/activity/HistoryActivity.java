/**
 * @file HistoryActivity.java
 * @brief 撮影履歴の画面
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/02/09
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.adapter.HistoryListAdapter;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDbHelper;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDbMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry._ID;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_DATE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_ITEM;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_LATITUDE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_LONGITUDE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_OBJECT;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.TXT_PERCENTAGE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDbMethod.QUERY_SELECT_ALL;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.savePath;

/**
 * @brief 撮影履歴の画面
 *
 * @par 概要
 * データベースから情報を読んでListViewに
 * 今までの撮影履歴を表示するクラスで
 * CursorAdapterと連結されています。
 */
public class HistoryActivity extends AppCompatActivity {

    /* 画面上のmenu bar */
    ActionBar actionBar;
    /* CSVファイル出力ボタン */
    Button btnPushCsv;
    /* データがない時表示されるTextView */
    TextView emptyTextView;
    /* 履歴のDB情報を持つクラス */
    HistoryDbHelper dbHelper;
    /* SQLite データベース */
    SQLiteDatabase db;
    /* データベースを読むCursor */
    Cursor mCursor;
    /* 撮影履歴を表示するListView */
    ListView historyList;
    /* CursorAdapterを継承したAdapter */
    HistoryListAdapter historyListAdapter;
    /* CSVファイルを書くFileOutputStream */
    FileOutputStream fos;
    /* CSVファイル名(保存日時) */
    String saveDate = new SimpleDateFormat("yyyyMMdd HHmm", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // menu barに前の画面に戻るボタンを見せる
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        // menu bar名を指定
        actionBar.setTitle(R.string.history);
        btnPushCsv = (Button)findViewById(R.id.btnPushCsv);
        historyList = (ListView)findViewById(R.id.historyList);
        emptyTextView = (TextView)findViewById(R.id.emptyTextView);
        // データベースを読むために呼び込む
        dbHelper = new HistoryDbHelper(this);
        db = dbHelper.getReadableDatabase();
        // CSV出力ボタンにOnClickListnerを付ける
        btnPushCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushCsv(v);
            }
        });
        // select句文で全てのデータを読み込む
        mCursor = db.rawQuery(QUERY_SELECT_ALL,null);
        historyListAdapter = new HistoryListAdapter(this, mCursor);
        // 保存データがない場合の画面設定
        historyList.setEmptyView(emptyTextView);
        // リストビューにadapter適応
        historyList.setAdapter(historyListAdapter);
        // リストの項目をクリックした時、詳細情報を見せてくれる
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
                // cursorで探した項目のpositionをidとして次の画面に渡す
                Cursor cursor = (Cursor) historyListAdapter.getItem(position);
                String index = cursor.getString(cursor.getColumnIndex(_ID));
                int id = Integer.parseInt(index);
                // _IDコラムで詳細情報を探すので、intentで次の画面に渡す
                intent.putExtra(_ID, id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ネビゲーションの"戻る"ボタンを押すと,検査項目名を保存したまま次の画面を起動します。
        switch(item.getItemId()){
            case android.R.id.home://"戻る"ボタンを押す場合の処理イベント
                Intent intent=new Intent(HistoryActivity.this,TopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * @brief CSVファイル出力
     *
     * @par 概要
     * ユーザーがボタンをクリックすると、
     * 端末の中に物理的なCSVファイルで
     * 撮影履歴全部をバックアップ
     *
     * @par 処理
     * データベースのテーブルを読み込んで
     * csvファイルにheaderとdataを入力し、
     * 指定した位置に保存します。
     *
     * @param v ビュー
     * @return なし
     */
    public void pushCsv(View v){

        try {
            // ファイルを保存するフォルダが存在しないと新しく作る
            if(!savePath.exists()){

                savePath.mkdir();
            }

            // FileOutputStreamでCSVファイル出力
            fos = new FileOutputStream(new File(savePath+"/"+saveDate+getString(R.string.csvExtension)));

            // CSVファイルにheaderを書く
            String header = String.format("%s,%s,%s,%s,%s,%s\n", TXT_DATE, TXT_ITEM, TXT_OBJECT, TXT_PERCENTAGE, TXT_LATITUDE, TXT_LONGITUDE);
            fos.write(header.getBytes());
            // select句文で全てのデータを読み込む
            Cursor mCursor = HistoryDbMethod.selectAll(db);
            // 列の情報を一つずつcsvファイルに書く
            for(int i=0; i<mCursor.getCount(); i++){
                // 撮影日時
                String date = mCursor.getString(0);
                // 検査項目
                String inspection_item = mCursor.getString(1);
                // 検査物品
                String inspection_object = mCursor.getString(2);
                // 確信度
                Double certainty_factor = mCursor.getDouble(3);
                // 位置情報(緯度)
                Double latitude = mCursor.getDouble(4);
                // 位置情報(軽度)
                Double longitude = mCursor.getDouble(5);
                // CSVファイルにdataを書く
                String content = String.format("%s,%s,%s,%s,%s,%s\n", date, inspection_item, inspection_object, certainty_factor+"%", latitude, longitude);
                fos.write(content.getBytes());
                mCursor.moveToNext();
            }
            fos.close();
            //出力の後、メッセージ表示
            Toast.makeText(this, R.string.csvMsg, Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}