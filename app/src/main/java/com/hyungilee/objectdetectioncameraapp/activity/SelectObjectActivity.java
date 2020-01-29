/**
 *@file  SelectObjectActivity.java
 *@brief メイン画面で撮影ボタンをクリックした後に見えるリストビュー画面を処理するクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;

import androidx.annotation.Nullable;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.fragment.ObjectListFragment;


/**
 * @brief リストビューを含むフレグメントで構成されるレイアウトを表示するクラス
 *
 * @par 概要
 * 基本的に画面の上段にナビゲーションバーを含んでいます。
 * レイアウトのフラグメント部分には二つのリストビューがreplaceされて表示します。
 */
public class SelectObjectActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_object);

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(R.id.container, new ObjectListFragment());

            transaction.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        /* menu barの名前を指定 */
        actionBar.setTitle(R.string.selectMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ネビゲーションの"戻る"ボタンを押すと,検査項目名を保存したまま次の画面を起動します。
        switch(item.getItemId()){
            case android.R.id.home://"戻る"ボタンを押す場合の処理イベント
                Intent intent=new Intent(SelectObjectActivity.this,TopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
