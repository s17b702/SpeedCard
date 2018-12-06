/**
 * Copyright (c) 2014 groupN カードプロジェクト
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtaion a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ac.shohoku.android.speedcard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 一枚一枚のカードを表すクラス
 * @author 山上賢司
 * @version 0.1
 */
public class card {
    private Bitmap mBmp = null;
    private Rect mLocation;
    private boolean mIsTapped;
    private int mW, mH;

    /**
     * カードのコンストラクタ
     * @param sview リソースを読み込むため、SpeedCardViewを読み込む
     * @param cardName カード名からBitmapを読み込む
     */
    public Card(SpeedCardView sview, String cardName){
        Resources rs = sview.getResources(); //リソースを取得
        Context context = sview.getContext(); //パッケージ名を取得するためにContextを取得
        int resId = rs.getIdentifier(cardName, "drawable", context.getPackageName());
        mBmp  = BitmapFactory.decodeResource(rs, resId); //画像を取得
        mW = mBmp.getWidth();
        mH = mBmp.getHeight();
        setmLocation(0,0,mW,mH); //いったん左上に配置
        mIsTapped = false; //タップされていない
    }
    /**
      * カードの位置を設定する
      * @param left 左上の x 座標
      * @param top 左上の y 座標
      * @param right 右下の x 座標
      * @param bottom 右下の y 座標
      */
    public void setmLocation(int left, int top, int right, int bottom) {
        mLocation = new Rect(left, top, right, bottom);
    }
    /**
      * 長方形をタップされたかどうかをチェックする．タップされていれば true，そうでなければ false
      * @param x タップされた位置の x 座標
      * @param y タップされた位置の y 座標
      * @return タップされたかどうか
      */
         public boolean checkTapped(int x, int y) {
            if (mLocation.left < x && x < mLocation.right &&
                   mLocation.top < y && y < mLocation.bottom) {
                return true;
             } else {
             return false;
             }
         }
         /**
           * タップを設定
           * @param b true or false
           */
         public void setTapped(boolean b) {
         mIsTapped = b;
         }

         /**
           * カードがタップされているかどうかをチェックする
           */
         public boolean isTapped() {
         return mIsTapped;
         }

         /**
           * カードの幅を返す
           * @return
           */
         public int getW() {
         return mW;
         }

         /**
           * カードの高さを返す
           * @return
           */
         public int getH() {
         return mH;
         }

         /**
           * カードの画像を描画する
           * @param canvas
           */
         public void draw(Canvas canvas) {
         float left = mLocation.left;
         float top = mLocation.top;
         if (mBmp != null) {
             canvas.drawBitmap(mBmp, left, top, new Paint()); //カード画像の描画

         }
    }
}
