/**
 * @file TopActivity.java
 * @brief アプリのトップ画面
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hyungilee.objectdetectioncameraapp.R;

/**
 * @brief アプリのトップ画面
 *
 * @par 概要
 * アプリのメイン画面として、Splash以後に現れる。
 * 撮影、撮影履歴、設定のメニューボタンを持つ。
 */

public class TopActivity extends AppCompatActivity implements Button.OnClickListener {

    /* 撮影ボタン */
    Button btnCamera;
    /* 撮影履歴ボタン */
    Button btnHistory;
    /* 設定ボタン、設定画面に移動 */
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //メイン画面を起動する前にSplash画面で1秒間遅延処理。
        try{
            Thread.sleep(1000);//1秒後にTopActivityを起動する
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_top);

        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnHistory = (Button)findViewById(R.id.btnHistory);
        btnSettings = (Button)findViewById(R.id.btnSettings);

        // 三つのボタンにOnClickListenerを追加
        btnCamera.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
    }

    /**
     * @brief 別々のボタンのOnClickメソッド
     *
     * @par 概要
     * 三つのボタンの別々のOnClickメソッドとして、
     * 撮影画面、撮影履歴画面、設定画面へ移動する。
     *
     * @par 処理
     * ボタンのIDを条件として、
     * Intentで指定された画面に移動する。
     *
     * @param v ビュー
     * @return なし
     */
    public void onClick(View v){

        // IDを基準としてswitch文で分化
        switch (v.getId()){

            // 撮影物を選ぶ画面に移動
            case R.id.btnCamera:
                break;

            // 撮影履歴画面に移動
            case R.id.btnHistory:
                break;

            // 設定画面に移動
            case R.id.btnSettings:
                break;
        }
    }

}
