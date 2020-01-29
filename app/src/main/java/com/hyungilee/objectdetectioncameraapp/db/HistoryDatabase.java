/**
 * @file HistoryDatabase.java
 * @brief データベースの情報を持つクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.db;

import android.provider.BaseColumns;

/**
 * @brief データベースに必要な情報を持つクラス
 *
 * @par 概要
 * データベース名とバージョン情報、
 * そしてテーブル名とテーブルを構成するcolumn名を
 * 指定、Stringとして保存しています。
 */

public class HistoryDatabase {

    /** データベース名 */
    public static final String DB_NAME = "objectdetectioncameraapp.db";

    /** データベースのバージョン */
    public static final int DB_VERSION = 1;

    /** 必要なString定数 */
    public static final String TXT_DATE = "撮影日";
    public static final String TXT_ITEM = "検査項目";
    public static final String TXT_OBJECT = "検査物品";
    public static final String TXT_PERCENTAGE = "確信度";
    public static final String TXT_LATITUDE = "位置情報（緯度）";
    public static final String TXT_LONGITUDE = "位置情報（経度）";


    /**
     * @brief テーブルに必要なtextを持つクラス
     *
     * @par 概要
     * テーブル名とコラムの名前をStringで持ち、
     * 他のクラスでデータベースを呼ぶ時利用できるようにする。
     */
    public class HistoryEntry implements BaseColumns {

        // テーブル名
        public static final String TABLE = "histories";

        // 各情報を職別できるid
        public static final String _ID = "_id";

        // 保存される写真名
        public static final String COL_HISTORY_PIC = "picture_name";

        // 写真の撮影日時
        public static final String COL_HISTORY_DATE = "date";

        // 検査項目
        public static final String COL_HISTORY_ITEM = "inspection_item";

        // 検査物品
        public static final String COL_HISTORY_OBJECT = "inspection_object";

        // 確信度
        public static final String COL_HISTORY_PERCENTAGE = "certainty_factor";

        // 位置情報(緯度)
        public static final String COL_HISTORY_LATITUDE = "latitude";

        // 位置情報(軽度)
        public static final String COL_HISTORY_LONGITUDE = "longitude";
    }

}
