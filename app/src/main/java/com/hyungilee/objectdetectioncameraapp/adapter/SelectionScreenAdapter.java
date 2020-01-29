/**
 * @file SelectionScreenAdapter.java
 * @brief リストビューで見せるコンテンツを初期化するAdapter
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyungilee.objectdetectioncameraapp.R;
import com.hyungilee.objectdetectioncameraapp.model.ContentsListItem;

import java.util.ArrayList;

/**
 * @brief SelectObjectリストに情報をセッティングして送るAdapterクラス
 *
 * @par 概要
 * ContentsListItemの写真と項目名をセッティングしてAdapterを初期化。
 *
 */
public class SelectionScreenAdapter extends BaseAdapter {

    Context context;

    int layout;

    /*ContentsListItemのオブジェクト情報を入れるArrayList変数*/
    ArrayList<ContentsListItem> list;

    /*xmlのレイアウトをオブジェクト化するためのinflater変数*/
    LayoutInflater inflater;

    public SelectionScreenAdapter(Context context, int layout, ArrayList<ContentsListItem> list, LayoutInflater inflater){
        super();
        this.context=context;
        this.layout=layout;
        this.list=list;
        this.inflater=inflater;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        //ViewタイプのconvertView変数が nullじゃない場合は
        if(convertView==null){
            //inflaterを通じてレイアウトをオブジェクト化してconvertView変数に入れます。。
            convertView=inflater.inflate(R.layout.activity_single_item,null);
        }
        /*ImageViewタイプのimageView変数を初期化*/
        ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);

        /*TextViewタイプのtextView変数を初期化*/
        TextView textView=(TextView)convertView.findViewById(R.id.textView);

        /*ArrayListのindex位置を通じてContentsListItemのオブジェクトに情報を入れます。*/
        ContentsListItem item=list.get(position);

        /*ImageViewタイプのimageView変数に写真リソースをセッティング*/
        imageView.setImageResource(item.getResId());

        /*textViewタイプのtextView変数に項目名をセッティング*/
        textView.setText(item.getName());

        /*セッティングしたViewをreturnします。*/
        return convertView;
    }

}
