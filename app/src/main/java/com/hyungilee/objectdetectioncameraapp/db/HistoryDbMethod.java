/**
 * @file HistoryDbMethod.java
 * @brief データベースのqueryを処理するクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.hyungilee.objectdetectioncameraapp.db.HistoryDatabase.HistoryEntry.TABLE;

/**
 * @brief データベースのqueryを処理するクラス
 *
 * @par 概要
 * データベースから情報を探す、削除する
 * メソッドを持つクラス
 */

public class HistoryDbMethod {

    /* データベースの情報全てを読み込むselect文 */
    public final static String QUERY_SELECT_ALL = String.format("SELECT * FROM %s", TABLE);


    /**
     * @brief データ全部を読み込む
     *
     * @par 概要
     * CSVファイルの出力のため、
     * id以外の情報を全部読み込んで
     * そのcursorをreturnする
     *
     * @par 処理
     * データベースのqueryで
     * id以外の情報をStringの配列で
     * cursorが読む形として、そのcursorをreturn
     *
     * @param db
     * @return Cursor
     */
    public final static Cursor selectAll(SQLiteDatabase db){
        Cursor mCursor = db.query(TABLE,
                new String[]{HistoryDatabase.HistoryEntry.COL_HISTORY_DATE, HistoryDatabase.HistoryEntry.COL_HISTORY_ITEM, HistoryDatabase.HistoryEntry.COL_HISTORY_OBJECT,
                        HistoryDatabase.HistoryEntry.COL_HISTORY_PERCENTAGE, HistoryDatabase.HistoryEntry.COL_HISTORY_LATITUDE, HistoryDatabase.HistoryEntry.COL_HISTORY_LONGITUDE},
                null, null, null, null, null);
        mCursor.moveToFirst();

        return mCursor;
    }


    /**
     * @brief 一つの情報を読む
     *
     * @par 概要
     * 詳細情報画面でユーザが選択した
     * 情報を読んで画面に表示する
     *
     * @par 処理
     * idの条件をStringでもらって
     * データベースから検索、row全てを
     * Stringの配列で読むcursorをreturnする
     *
     * @param db, where
     * @return Cursor
     */
    public final static Cursor readOneHistory(SQLiteDatabase db, String where){
        Cursor mCursor = db.query(TABLE,
                new String[]{HistoryDatabase.HistoryEntry.COL_HISTORY_PIC, HistoryDatabase.HistoryEntry.COL_HISTORY_DATE, HistoryDatabase.HistoryEntry.COL_HISTORY_ITEM, HistoryDatabase.HistoryEntry.COL_HISTORY_OBJECT,
                        HistoryDatabase.HistoryEntry.COL_HISTORY_PERCENTAGE, HistoryDatabase.HistoryEntry.COL_HISTORY_LATITUDE, HistoryDatabase.HistoryEntry.COL_HISTORY_LONGITUDE},
                where, null, null, null, null);

        mCursor.moveToFirst();

        return mCursor;
    }


    /**
     * @brief イメージのファイル名をselectするメソッド
     *
     * @par 概要
     * DBから特定のIDで検索し、
     * イメージファイル名をreturnする
     *
     * @par 処理
     * select文でイメージのpicture_nameコラムを検索し、
     * それをStringでreturnする
     *
     * @param db, id
     * @return String
     */
    public final static String selectPicName(SQLiteDatabase db, int id){

        /* select文でpicture_nameと言うコラムを検索 */
        String selectSql = "select picture_name from "+TABLE+" where _id = "+id+";";
        Cursor mCursor = db.rawQuery(selectSql, null);
        mCursor.moveToFirst();

        String picName = mCursor.getString(0);
        return picName;
    }

}
