/**
 * @file ConstantClass.java
 * @brief Constant変数を宣言するクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */
package com.hyungilee.objectdetectioncameraapp.utility;

import android.os.Environment;
import java.io.File;


/**
 * @brief Constant変数を宣言するクラス
 *
 * @par 概要
 *
 * 各クラスで共通に使われている変数をstatic常數で宣言します。
 *
 */
public class Constant {

    /* HistoryActivity.java / CameraPreviewActivity.java */

    /* 撮った写真とCSVファイルを保存する時保存経路をセッティングに使いう変数 */
    public static final String EXTRA_STRING_SAVE_FOLDER_NAME="/NTT-East";

    /* 撮影した写真とCSVファイルとイメージが保存される位置 */
    public static final File savePath = Environment.getExternalStoragePublicDirectory(EXTRA_STRING_SAVE_FOLDER_NAME);



    /*intent宣言する時,次のページに送る変数のkeyの部分を常数化*/

    /* CameraPreviewActivity.java: */
    /*写真が保存される時の時間情報を保存する変数*/
    public static final String EXTRA_STRING_TIME_STAMP="timeStamp";

    /*写真が保存されるルートを保存する変数*/
    public static final String EXTRA_STRING_IMAGE_FILE_PATH="filePath";

    /*保存される写真ファイル名を保存する変数*/
    public static final String EXTRA_STRING_FILE_NAME="fileName";


}
