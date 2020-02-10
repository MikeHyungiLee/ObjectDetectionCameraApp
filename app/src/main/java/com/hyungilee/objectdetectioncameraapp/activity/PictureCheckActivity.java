/**
 * @file PictureCheckActivity.java
 * @brief 写真を撮った後で起動される画面
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase;
import com.hyungilee.objectdetectioncameraapp.db.HistoryDbHelper;

import java.io.File;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.hyungilee.objectdetectioncameraapp.fragment.ObjectListFragment.EXTRA_STRING_SELECT_MENU;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_FILE_NAME;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_IMAGE_FILE_PATH;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_TIME_STAMP;


/**
 * @brief 写真を撮った後,写真や写真に関する情報を画面に表示してくれる部分を処理するクラス
 *
 * @par 概要
 * 写真を撮った後で写真の情報を表示する画面クラス
 * 画面に表示する情報は撮影日時、検査項目、検査物品、確信度、位置情報があります。
 * 画面で再撮影ボタンと保存ボタンで構成されています。
 * 再撮影ボタンを押すと選択した項目名を維持してカメラ撮影画面に移動します。
 * 保存ボタンを押すと表示されたデータ値をHistoryDatabaseクラスで宣言された
 * HistoryEntryクラスを通じて,データベースのコラムに保存します。
 */
public class PictureCheckActivity extends AppCompatActivity {

    /*ロケーションプロバイダーとそうごさようするためのメインエントリポイント*/
    private FusedLocationProviderClient client;

    /*撮った写真表示*/
    ImageView imageview;

    /** 写真の撮影日時 */
    TextView date;

    /*検査項目*/
    TextView inspection_item;

    TextView inspection_object;

    /** 確信度 */
    TextView certainty_factor;

    /** 位置情報(緯度) */
    TextView latitude;

    /*位置情報(軽度)*/
    TextView longitude;

    /*写真の撮影日時(YYYY/MM/DD HH:MM)*/
    TextView dateText;

    /*端末に保存された写真のファイル名*/
    String fileName;

    /*SelectObjectActivity画面で選択した項目名*/
    String menu;

    /** データ保存のため、HistoryDbHelper.javaを読んでくる */
    private HistoryDbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_check);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        /** menu barの名前を指定 */
        actionBar.setTitle(R.string.check);

        dbHelper = new HistoryDbHelper(this);

        imageview=(ImageView)findViewById(R.id.picture);
        date = (TextView)findViewById(R.id.date);
        inspection_item = (TextView)findViewById(R.id.inspection_item);
        certainty_factor = (TextView)findViewById(R.id.certainty_factor);
        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        dateText=(TextView)findViewById(R.id.date);

        /*CameraPreviewActivityからIntentを通じて送った写真の情報をもらって変数に保存します。
        * 撮影日時、検査項目、検査物品、ファイル名*/
        Intent intent=getIntent();
        String date = intent.getStringExtra(EXTRA_STRING_TIME_STAMP);
        menu = intent.getStringExtra(EXTRA_STRING_SELECT_MENU);
        String path = intent.getStringExtra(EXTRA_STRING_IMAGE_FILE_PATH);
        fileName=intent.getStringExtra(EXTRA_STRING_FILE_NAME);

        /*写真を保存する時名前で使用したフォーメット情報をsubstringして年、月、日、時、分で分けます。*/
        String year=date.substring(0,4);
        String month=date.substring(4,6);
        String day=date.substring(6,8);
        String hour=date.substring(9,11);
        String minutes=date.substring(11,13);

        /*日付表記部分に上記の年,月,日情報表記*/
        dateText.setText(year+"/"+month+"/"+day+" "+hour+":"+minutes);

        /*選択した項目名を表記する。*/
        inspection_item.setText(menu);

        /** 確信度を表示する。*/
        certainty_factor.setText(Double.toString(99.9));

        /*写真を撮った後,保存されたファイル経路を利用してdecodeFileを利用してBitmapファイルに変換します。
        その後,写真をImageViewに表示します。*/
        File imgFile=new File(path);

        /*保存された写真のファイル経路を利用してファイルオブジェクトを作り,exists()メソッドを利用してファイルの有無をチェックする。
        (ある場合にはファイルをBitmapに変換)*/
        if(imgFile.exists()){
            Bitmap pictureBitmap= BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageview.setImageBitmap(pictureBitmap);
        }

        /*ロケーションプロバイダーと相互作用するためのメインエントリポイント*/
        client = LocationServices.getFusedLocationProviderClient(this);

        // GPS情報をもらうために必要なPermissionチェック
        if (ActivityCompat.checkSelfPermission(PictureCheckActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        //ロケーションプロバイダーを使うために宣言したclient変数を使って、イベント処理をします。
        client.getLastLocation().addOnSuccessListener(PictureCheckActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            //もしparameter valueとしてもらったlocationがnullじゃなかい場合は
            //もらったlocation変数を通じて緯度と軽度の情報をもらってTextViewにセットします。
                if(location!=null){
                    longitude.setText(Double.toString(location.getLongitude()));
                    latitude.setText(Double.toString(location.getLatitude()));
                }else{
                    //もらったlocation変数がnullの場合は"null"Toast messageを見せます。
                    Toast.makeText(PictureCheckActivity.this,R.string.locationMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ネビゲーションの"戻る"ボタンを押すと,検査項目名を保存したまま次の画面を起動します。
        switch(item.getItemId()){
            case android.R.id.home://"戻る"ボタンを押す場合の処理イベント
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *@brief PictureCheckActivity内部にあるボタンのイベント処理
     *
     *@par 概要
     *再撮影ボタンと保存ボタンイベント処理
     *
     *@par 処理
     *ボタンをクリックすると,switch-case文である該当ボタンのイベントが処理される。
     *
     *@param
     *@return なし
     */
    public void onClick(View v){
        switch (v.getId()){

            case R.id.btnRetake:
                /** 再撮影ボタン */
                Intent intent=new Intent(PictureCheckActivity.this,CameraPreviewActivity.class);
                /*イメージファイルの情報を初期化*/
                intent.putExtra(EXTRA_STRING_FILE_NAME,"");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                /*検査項目名保存*/
                intent.putExtra(EXTRA_STRING_SELECT_MENU,menu);
                startActivityForResult(intent,0);

                break;

            case R.id.btnSave:
                /** 保存ボタン */
                savePic();
                Intent toTop = new Intent(this, TopActivity.class);

                /** 移動する時、そのstackの中のactivityを全部消す */
                toTop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                /** 動するactivityがもう実行中なら、それを再使用する */
                toTop.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                Toast.makeText(PictureCheckActivity.this,R.string.returnMsg, Toast.LENGTH_LONG).show();

                startActivity(toTop);

                break;
        }
    }

    /** ユーザが撮影した写真と情報をデータベースに保存する */
    public void savePic(){

        /** 写真の保存名(今は任意のタイトルで) */
        String picName = fileName;

        /** 写真の撮影日時 */
        String picDate = date.getText().toString();

        /*検査項目*/
        String picItem = inspection_item.getText().toString();

        /*検査物品*/
        String picObject=inspection_object.getText().toString();

        /** 確信度 */
        Double picPercentage = Double.parseDouble(certainty_factor.getText().toString());

        Double picLat=0.0d;
        Double picLong=0.0d;

        try{
            /** 位置情報(緯度) */
            picLat = Double.parseDouble(latitude.getText().toString());
            /*位置情報(軽度)*/
            picLong = Double.parseDouble(longitude.getText().toString());
        }catch(NumberFormatException e){
            e.printStackTrace();
            picLat=0.0d;
            picLat=0.0d;
        }

        /** 生成されたテーブルに情報を書けるようにする */
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /** 新しい情報を入力すると宣言 */
        ContentValues values = new ContentValues();

        /** 今は任意のデータを入力してみます。 */
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_PIC, picName);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_DATE, picDate);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_ITEM, picItem);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_OBJECT,picObject);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_PERCENTAGE, picPercentage);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_LATITUDE, picLat);
        values.put(HistoryDatabase.HistoryEntry.COL_HISTORY_LONGITUDE, picLong);

        /** 情報をテーブルにinsertする */
        try{
            db.insertOrThrow(HistoryDatabase.HistoryEntry.TABLE, null, values);
        }catch(SQLException e){
            e.printStackTrace();
        }

        db.close();
    }
}