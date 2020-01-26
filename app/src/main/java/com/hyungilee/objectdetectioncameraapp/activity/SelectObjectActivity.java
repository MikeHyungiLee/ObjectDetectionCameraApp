/**
 *@file  SelectObjectActivity.java
 *@brief メイン画面で撮影ボタンをクリックした後に見えるリストビュー画面を処理するクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.fragment.ObjectListFragment;

/**
 * @brief アプリのトップ画面
 *
 * @par 概要
 * アプリのメイン画面として、Splash以後に現れる。
 * 撮影、撮影履歴、設定のメニューボタンを持つ。
 */
public class SelectObjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_object);

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(R.id.container, new ObjectListFragment());

            transaction.commit();
        }
    }
}
