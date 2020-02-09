/**
 * @file HistoryDetailActivity.java
 * @brief 詳細履歴を表示するクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/02/09
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDbHelper;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDbMethod;
import java.io.File;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry.TABLE;
import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry._ID;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.savePath;

/**
 * @brief 詳細履歴を表示するクラス
 *
 * @par 概要
 * 詳細履歴を見せてくれる画面として、
 * データベースから写真名、撮影日時、検査項目、
 * 確信度、緯度、軽度をもらって表示する。
 * 削除ボタンを押すと当該履歴を削除する。
 */
public class HistoryDetailActivity extends AppCompatActivity {

    /* 画面上のmenu bar */
    ActionBar actionBar;
    /* 詳細情報の写真 */
    ImageView imageDetailPic;
    /* 撮影日時 */
    TextView textDetailDate;
    /* 検査項目 */
    TextView textDetailName;
    /* 検査物品 */
    TextView textDetailObject;
    /* 確信度 */
    TextView textDetailPercentage;
    /* 位置情報(緯度) */
    TextView textDetailLatitude;
    /* 位置情報(軽度) */
    TextView textDetailLongitude;
    /* 履歴削除ボタン */
    Button btnDelete;
    /* 履歴のDB情報を持つクラス */
    HistoryDbHelper dbHelper;
    /* SQLiteデータベース */
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        imageDetailPic = (ImageView)findViewById(R.id.detailPic);
        textDetailDate = (TextView)findViewById(R.id.detailDate);
        textDetailName = (TextView)findViewById(R.id.detailName);
        textDetailObject = (TextView)findViewById(R.id.detailObject);
        textDetailPercentage = (TextView)findViewById(R.id.detailPercentage);
        textDetailLatitude = (TextView)findViewById(R.id.detailLatitude);
        textDetailLongitude = (TextView)findViewById(R.id.detailLongitude);
        btnDelete = (Button)findViewById(R.id.btnDelete);

        // menu barに前の画面に戻るボタンを見せる
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 画面のonCreateができたらユーザがクリックした情報の詳細内容を読むメソッド
        readHistoryDetail();
        // 削除ボタンを押した時のメソッド
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 詳細情報を削除する
                deleteOneDetail();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ネビゲーションの"戻る"ボタンを押すと,検査項目名を保存したまま次の画面を起動します。
        switch(item.getItemId()){
            case android.R.id.home://"戻る"ボタンを押す場合の処理イベント
                Intent intent=new Intent(HistoryDetailActivity.this,HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * @brief 詳細履歴情報を照会
     *
     * @par 概要
     * データベースからユーザーがクリックしたpositionの
     * 情報を持ってきて、別々のimageviewとtextviewに
     * 表示します。
     *
     * @par 処理
     * 前の画面でユーザーがクリックした位置をidでもらい、
     * それを基準でデータベースから同じidのcolumnを持ってきます。
     * 情報を別々のimageviewやtextviewにsetします。
     *
     * @param
     * @return なし
     */
    public void readHistoryDetail(){
        /* コラムを特定のIDで読み込むので前の画面でクリックしたpositionを持ってくる */
        Intent intent = getIntent();
        int id = intent.getIntExtra(_ID, 0);
        /*DBからデータを読み込む*/
        dbHelper = new HistoryDbHelper(this);
        db = dbHelper.getReadableDatabase();
        /*idで特定の情報を検索する*/
        String where = "_id = "+id;
        /* そのidの詳細情報を読み込む */
        Cursor mCursor = HistoryDbMethod.readOneHistory(db, where);
        /* 写真の保存名 */
        String picture_name = mCursor.getString(0);
        /* 撮影日時 */
        String date = mCursor.getString(1);
        /* 検査項目 */
        String inspection_item = mCursor.getString(2);
        /* 検査物品 */
        String inspection_object = mCursor.getString(3);
        /* 確信度 */
        Double certainty_factor = mCursor.getDouble(4);
        /* 位置情報(緯度) */
        Double latitude = mCursor.getDouble(5);
        /* 位置情報(軽度) */
        Double longitude = mCursor.getDouble(6);
        /* 撮影日時をmenu bar名で指定 */
        actionBar.setTitle(date);
        /* イメージがある場合はそれをImageViewに表示 */
        File imgFile = new File(savePath, picture_name);
        if(imgFile.exists()){
            Bitmap pictureBitmap= BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageDetailPic.setImageBitmap(pictureBitmap);
        /* イメージが存在しない場合はNoImageAvailableを表示 */
        } else{
            Bitmap noImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.noimage);
            imageDetailPic.setImageBitmap(noImage);
        }
        /* 詳細情報を別々のtextviewに入れる */
        textDetailDate.setText(date);
        textDetailName.setText(inspection_item);
        textDetailObject.setText(inspection_object);
        textDetailPercentage.setText(Double.toString(certainty_factor)+"%");
        textDetailLatitude.setText(Double.toString(latitude));
        textDetailLongitude.setText(Double.toString(longitude));
    }


    /**
     * @brief 詳細履歴情報を削除
     *
     * @par 概要
     * 詳細情報画面でユーザーが削除ボタンを押すと、
     * その特定のデータがデータベースから削除されます。
     *
     * @par 処理
     * 前の画面からもらってきたidをkeyとして、
     * データベースから同じidのコラムを持っている
     * rowをデータベースから削除します。
     *
     * @param
     * @return なし
     */
    public void deleteOneDetail(){

        /* ユーザが削除ボタンを押した時、AlertDialogでもう一度確認する */
        AlertDialog.Builder deleteConfirm = new AlertDialog.Builder(HistoryDetailActivity.this);
        deleteConfirm.setMessage(R.string.deleteMsg).setCancelable(false).setPositiveButton(R.string.check,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* 削除を押した場合、データベースから情報を削除、Top画面に戻る */
                        /* コラムを特定のIDで削除するので前の画面でクリックしたpositionを持ってくる */
                        Intent intent = getIntent();
                        int id = intent.getIntExtra(_ID, 0);
                        /* DBからデータを読み込む */
                        db = dbHelper.getWritableDatabase();
                        /* idが同じコラムを検索して消すdelete文 */
                        String str = "DELETE FROM "+TABLE+" WHERE _id = "+id+";";
                        /* DBに保存されているイメージのファイル名を読み込む */
                        String picName = HistoryDbMethod.selectPicName(db, id);
                        File imgFile =  new File(savePath, picName);
                        /* pathにイメージがある場合削除する */
                        if(imgFile.exists()){
                            imgFile.delete();
                        }
                        /* データベースでデータを削除する */
                        db.execSQL(str);
                        db.close();
                        /* リスト画面に戻る */
                        Intent listIntent = new Intent(getBaseContext(), HistoryActivity.class);
                        startActivity(listIntent);
                    }
                }).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* 取消を押した場合はreturnする */
                        return;
                    }
                });
        AlertDialog alert = deleteConfirm.create();
        alert.show();
    }

}