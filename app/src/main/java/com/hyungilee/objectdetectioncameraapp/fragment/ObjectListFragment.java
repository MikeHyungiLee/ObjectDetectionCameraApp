/**
 * @file ObjectListFragment.java
 * @brief 1つのActivityに表示されるFragment画面
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.activity.CameraPreviewActivity;
import com.hyungilee.objectdetectioncameraapp.adapter.SelectionScreenAdapter;
import com.hyungilee.objectdetectioncameraapp.model.ContentsListItem;

import java.util.ArrayList;

/**
 * @brief フラグメントにリストビューを出力し,二つのフラグメントのようにreplaceして出力。
 *
 * @par 概要
 * 同じフレグメントの部分に2つのリストビューを交互に見せるように処理
 * 最初のフラグメントには"下部支線"と"分線用金物"がリストビューで出力
 * 二つ目に出力されるフラグメントには"支線アパンカ","スパンのポルト",
 * "クロロアランカ","軽犯罪"で構成されて出力
 */
public class ObjectListFragment extends Fragment {

    public static final String EXTRA_STRING_SELECT_MENU="selectMenu";

    /*フラグメントに出力するリストビュー*/
    ListView listView;
    /*リストビューに出力するアイテムリスト名を保存するArrayList*/
    ArrayList<ContentsListItem> contentsListItems=new ArrayList<>();
    /*フラグメント編集のためのフラグメントマネジャーのオブジェクト*/
    FragmentManager fragmentManager;
    /*フラグメント編集メソッドを呼び出すために使うフラグメントトランザクション*/
    FragmentTransaction fragmentTransaction;
    /*リストビューにセットするアデプター*/
    SelectionScreenAdapter selectionScreenAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*フラグメントマネジャー初期化*/
        fragmentManager=getFragmentManager();
        /*フラグメントトランザクション初期化*/
        fragmentTransaction=fragmentManager.beginTransaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*レイアウトをinflateしてオブジェクト化*/
        View fview = inflater.inflate(R.layout.fragment_section, container, false);

        listView=fview.findViewById(R.id.list_view_section);

        /*アデプタをアデプタに出力するリストビューレイアウトと,内部に入れるアイテム情報が保存されたArrayListを入れて初期化*/
        selectionScreenAdapter=new SelectionScreenAdapter(getActivity(),R.layout.activity_single_item,contentsListItems,getLayoutInflater());

        /*adapterにContentsListItemオブジェクトを生成及び初期化してコンテンツを追加*/
        contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list5)));
        contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list6)));

        /*作ったadapterをlistViewにセッティング*/
        listView.setAdapter(selectionScreenAdapter);

        //リストビューのアイテムクリックイベント処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //選択したリストの項目名が"下部支線"なら実行する条件文
                if(contentsListItems.get(i).getName().equals(getString(R.string.list5))){

                    /*ArrayList値初期化*/
                    contentsListItems.clear();

                    /*adapterにContentsListItemオブジェクトを生成及び初期化してコンテンツを追加*/
                    contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list1)));
                    contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list2)));
                    contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list3)));
                    contentsListItems.add(new ContentsListItem(R.drawable.ic_launcher_background,getString(R.string.list4)));

                    /*Adapterデータ変更処理*/
                    selectionScreenAdapter.notifyDataSetChanged();

                    /*変更されたAdapterをListViewに適用*/
                    listView.setAdapter(selectionScreenAdapter);

                    /*フラグメントTransactionにコミット*/
                    fragmentTransaction.commit();

                    /*変更されたフレグメントのリストのイベント処理*/
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent cameraPreview=new Intent(view.getContext(),CameraPreviewActivity.class);
                            /*メニュー選択時,カメラ撮影画面起動*/
                            /*次の画面を起動する前に選択された項目名を一緒に送ります。*/
                            cameraPreview.putExtra(EXTRA_STRING_SELECT_MENU,contentsListItems.get(position).getName());
                            startActivityForResult(cameraPreview,0);
                        }
                    });

                    /*最初のフラグメント画面で"分線用金物"メニューをクリックするとカメラ起動画面に移動*/
                }else if(contentsListItems.get(i).getName().equals(getString(R.string.list6))){

                    Intent cameraPreview=new Intent(view.getContext(), CameraPreviewActivity.class);
                    /*次の画面を起動する前に選択された項目名を一緒に送ります。*/
                    cameraPreview.putExtra(EXTRA_STRING_SELECT_MENU,getString(R.string.list6));
                    startActivity(cameraPreview);

                }

            }
        });

        return fview;
    }
}
