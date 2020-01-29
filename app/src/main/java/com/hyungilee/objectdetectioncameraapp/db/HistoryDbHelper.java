/**
 * @file HistoryDbHelper.java
 * @brief データベースの生成、アップグレードするメソッドを持つクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @brief データベースの生成、アップグレードするメソッドを持つクラス
 *
 * @par 概要
 * SQLiteのデータベースでユーザの写真とその情報を保存する
 * テーブルを生成する構文を定義しています。
 * そしてバージョンが変わった時、以前のテーブルを消して
 * 新たなテーブルを作るメソッドも含んでいます。
 */

public class HistoryDbHelper extends SQLiteOpenHelper {

    public HistoryDbHelper(Context context){
        super(context, HistoryDatabase.DB_NAME, null, HistoryDatabase.DB_VERSION);
    }


    /**
     * @brief テーブルを生成するメソッド
     *
     * @par 概要
     * SQLiteのテーブルを生成する。
     * コラムは固有なID、写真名、撮影日時、
     * 検査項目と物品、確信度、位置情報(緯度と軽度)です。
     *
     * @par 処理
     * SQL文でテーブルを生成します。
     *
     * @param db　データベース
     * @return なし
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + HistoryDatabase.HistoryEntry.TABLE + "(" +
                HistoryDatabase.HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_PIC + " TEXT NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_DATE + " TEXT NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_ITEM + " TEXT NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_OBJECT + " TEXT NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_PERCENTAGE + " INTEGER NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_LATITUDE + " INTEGER NOT NULL," +
                HistoryDatabase.HistoryEntry.COL_HISTORY_LONGITUDE + " INTEGER NOT NULL);";

        db.execSQL(createTable);
    }

    /* バージョンがアップグレードされた時、データベースを新しく生成するメソッド */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDatabase.HistoryEntry.TABLE);
        onCreate(db);
    }

}