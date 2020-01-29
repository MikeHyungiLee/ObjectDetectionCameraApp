/**
 * @file ContentsListItem.java
 * @brief リストビューで見せるコンテンツのオブジェクトクラス
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.model;


/**
 * @brief リストビューに表示される写真と項目名の情報が入っているオブジェクトクラス
 *
 * @par 概要
 * フィールド変数は,リストビューに表示される写真と項目名で構成されています。
 * フィールド変数のConstructorとGetterとSetterで構成されています。
 */
public class ContentsListItem {

    /*写真リソース変数*/
    int resId;
    /*項目名変数*/
    String name;

    /*フィールド変数のConstructor*/
    public ContentsListItem(int resId, String name){
        this.resId=resId;
        this.name=name;
    }
    /*写真リソース変数のGetter*/
    public int getResId() {
        return resId;
    }

    /*写真リソース変数のSetter*/
    public void setResId(int resId) {
        this.resId = resId;
    }

    /*項目名変数のGetter*/
    public String getName() {
        return name;
    }

    /*項目名変数のSetter*/
    public void setName(String name) {
        this.name = name;
    }

}
