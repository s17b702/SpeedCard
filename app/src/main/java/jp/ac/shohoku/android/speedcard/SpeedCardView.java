/**
 * Copyright (c) 2018 ndroid開発 カードプロジェクト
 * Licensed under the Apache License, Version 2.0 (the “Lisence”);
 * you may not use this file except in compliance with the License.
 * you may obtaion a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.ac.shohoku.android.speedcard;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author s17b702
 */
public class SpeedCardView extends SurfaceView implements Runnable, Callback {
    public static final int OPENING = 0; //オープニング画面
    public static final int LV1_DISP = 1; //レベル1 スタート表示
    public static final int LV1_PLAY = 2; //レベル1 プレイ中
    public static final int LV2_DISP = 3; //レベル2 スタート表示
    public static final int LV2_PLAY = 4; //レベル2 プレイ中
    public static final int LV3_DISP = 5; //レベル3 スタート表示
    public static final int LV3_PLAY = 6; //レベル3 プレイ中
    public static final int LV4_DISP = 7; //レベル4 スタート表示
    public static final int LV4_PLAY = 8; //レベル4 プレイ中
    public static final int GAME_OVER = 100; //ゲームオーバー
    public static final int GAME_CLEAR = 101; //ゲームクリア

    public static int NEXUS7_WIDTH = 0;
    public static int NEXUS7_HEIGHT = 0;
    private SurfaceHolder mHolder;
    private int mGameState; // ゲームの状態を表す変数

    private long mLvStart, mLvTime; //レベルの開始時間とレベルにいる時間
    private CardManager mCardManager; //カードマネージャー

    private final int LV1_CARD_MAX = 10; //レベル１で必要なカードの枚数
    private final int LV2_CARD_MAX = 20;
    private final int LV3_CARD_MAX = 30;
    private final int LV4_CARD_MAX = 50;

    private final int LV1_TIME = 7500; // レベル1 の時間
    private final int LV2_TIME = 15000; // レベル2 の時間
    private final int LV3_TIME = 22500; // レベル3 の時間
    private final int LV4_TIME = 37500; // レベル4 の時間

    private final int LV_TIME_MAX = 4000; // レベル滞在時間の最大値

    /**
     * コンストラクタ<br />
     * 引数は ContextとAttributeSet

     *
     * @param context
     * @param attrs
     */
    public SpeedCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    /**
     * 初期化用のメソッド<br />
     * 各種変数の初期化やコールバックの割り当てなどを行う
     */
    private void init() {
        mHolder = getHolder(); // SurfaceHolder を取得する．
        mHolder.addCallback(this);
        setFocusable(true); // フォーカスをあてることを可能にするメソッド
        requestFocus(); // フォーカスを要求して実行を可能にする
        mGameState = OPENING; //最初は OPENING 表示画面

    }

    /**
     * 定期的に実行するスレッドを生成し，定期的に実行の設定を行う<br />
     * このメソッドはサーフェスが生成されたタイミングで実行される．
     */
    private void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        //scheduledAtFixedRate の第 1 引数：実行可能なクラス．第 4 引数：ミリ秒に設定している
        //第 2 引数は実行を開始する時刻，第 3 引数は実行する間隔：
        executor.scheduleAtFixedRate(this, 30, 30, TimeUnit.MILLISECONDS);
    }

    /*
     * @see
     * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /*
     * サーフェスが生成されたとき，とりあえず画面に表示し，その後定期実行するスレッドをスタート
     *
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    public void surfaceCreated(SurfaceHolder holder) {
        draw();
        start();
    }

    /*
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * イベント処理するためのメソッド
     *
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) { //イベントの種類によって処理を振り分ける
            case MotionEvent.ACTION_DOWN: //画面上で押下されたとき
                switch (mGameState) { //ゲームの状態によって処理を振り分ける
                    case OPENING:
                        if(NEXUS7_WIDTH/2-100 < x && x < NEXUS7_WIDTH/2+100
                                && NEXUS7_HEIGHT/2-75 < y && y < NEXUS7_HEIGHT/2-25){ //ボタンの内部
                            mGameState = LV1_DISP;
                            mLvStart = System.currentTimeMillis();
                        }
                        break;
                    case LV1_DISP:
                        //mGameState = LV1_PLAY;
                        break;
                    case LV1_PLAY:
                        mCardManager.checkCards(x, y);
                        if (mCardManager.isFinished()) {
                            mGameState = LV2_DISP;
                            mLvStart = System.currentTimeMillis();
                        }
                        break;
                    case LV2_DISP:
                        //mGameState = LV2_PLAY;
                        break;
                    case LV2_PLAY:
                        mCardManager.checkCards(x, y);
                        if (mCardManager.isFinished()) {
                            mGameState = LV3_DISP;
                            mLvStart = System.currentTimeMillis();
                        }
                        break;
                    case LV3_DISP:
                        //mGameState = LV3_PLAY;
                        break;
                    case LV3_PLAY:
                        mCardManager.checkCards(x, y);
                        if (mCardManager.isFinished()) {
                            mGameState = LV4_DISP;
                            mLvStart = System.currentTimeMillis(); }
                        break;
                    case LV4_DISP:
                        //mGameState = LV4_PLAY;
                        break;
                    case LV4_PLAY:
                        mCardManager.checkCards(x, y);
                        if (mCardManager.isFinished()) {
                            mGameState = GAME_CLEAR;
                            mLvStart = System.currentTimeMillis(); }
                        break;
                    case GAME_OVER:
                        mGameState = OPENING;
                        break;
                    case GAME_CLEAR:
                        mGameState = OPENING;
                        break;
                }
                break;
        }
        return true;
    }

    /**
     * 描画用のメソッド<br />
     * 画面への描画処理はすべてこの中に書く
     */
    private void draw() {
        Canvas canvas = mHolder.lockCanvas(); // サーフェースをロック
        canvas.drawColor(Color.WHITE); // キャンバスを白に塗る
        String msg = null;
        Paint paint = new Paint();
        paint.setTextSize(30);
        float gw;
        NEXUS7_WIDTH = MainActivity.getViewWidth(); //SpeedCardView の幅を取得：いろいろな端末に対応
        NEXUS7_HEIGHT = MainActivity.getViewHeight();//SpeedCardView の高さを取得：いろいろな端末に対応

        switch (mGameState) { //ゲームの状態によって処理を振り分ける
            case OPENING:
                //オープニング画面の表示
                writeStartButton(canvas, paint); //スタートボタンの描画
                break;
            case LV1_DISP:
                //Log.v("sview", "SpeedCardView width=" + NEXUS7_WIDTH + ", height=" + NEXUS7_HEIGHT);
                //LV1_DISP の時の描画処理
                msg = "LEVEL1 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV1_PLAY:
                //LV1_PLAY の時の描画処理
                msg = "LEVEL1 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                mCardManager.draw(canvas);
                gw = (float) (LV1_TIME - mLvTime) / LV1_TIME * (NEXUS7_WIDTH - 20);
                canvas.drawRect(new Rect(10, 15, 10 + (int) gw, 20), paint);
                break;
            case LV2_DISP:
                //LV2_DISP の時の描画処理
                msg = "LEVEL2 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV2_PLAY:
                //LV2_PLAY の時の描画処理
                msg = "LEVEL2 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                mCardManager.draw(canvas);
                gw = (float) (LV2_TIME - mLvTime) / LV2_TIME * (NEXUS7_WIDTH - 20);
                canvas.drawRect(new Rect(10, 15, 10 + (int) gw, 20), paint);
                break;
            case LV3_DISP:
                //LV3_DISP 時の描画処理
                msg = "LEVEL3 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV3_PLAY:
                //LV3_PLAY の時の描画処理
                msg = "LEVEL3 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                mCardManager.draw(canvas);
                gw = (float) (LV3_TIME - mLvTime) / LV3_TIME * (NEXUS7_WIDTH - 20);
                canvas.drawRect(new Rect(10, 15, 10 + (int) gw, 20), paint);
                break;
            case LV4_DISP:
                //LV4_DISP の時の描画処理
                msg = "LEVEL4 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV4_PLAY:
                //LV4_PLAY の時の描画処理
                msg = "LEVEL4 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                mCardManager.draw(canvas);
                gw = (float) (LV4_TIME - mLvTime) / LV4_TIME * (NEXUS7_WIDTH - 20);
                canvas.drawRect(new Rect(10, 15, 10 + (int) gw, 20), paint);
                break;
            case GAME_OVER:
                //GAME_OVER 時の描画処理
                msg = "GAME OVER!! Tap to retry";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case GAME_CLEAR:
                //GAME_CLEAR の時の描画処理
                msg = "GAME CLEAR!! Tap to retry";
                canvas.drawText(msg, 10, 50, paint);
                break;
        }
        mHolder.unlockCanvasAndPost(canvas); // サーフェースのロックを外す
    }

    /**
     * スタートボタンの表示
     * @param canvas
     * @param paint
     */
    private void writeStartButton(Canvas canvas, Paint paint) {
        int left = NEXUS7_WIDTH/2 - 100;
        int top = NEXUS7_HEIGHT/2 - 75;
        int right = left + 200;
        int bottom = top + 50;
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawText("START", left + 60, bottom - 10, paint);
    }

    /**
     * カウントダウンの表示をします．
     * @param canvas
     */
    private void countDown(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(100);
        canvas.drawText("" + ((3000 - mLvTime) / 1000 + 1), 190, 527, paint); //画面の中心付近に
    }

    /*
     * 実行可能メソッド．このクラスの中では定期実行される
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        mLvTime = System.currentTimeMillis() - mLvStart;

        if (mLvTime >= 3000) { //3 秒経過したら状態を変更する
            switch (mGameState) {
                case LV1_DISP:
                    mGameState = LV1_PLAY;
                    mCardManager = new CardManager(this, LV1_CARD_MAX);
                    break;
                case LV1_PLAY:
                    if (mLvTime > LV1_TIME) { //レベル 1 の設定時間を過ぎたら
                        mGameState = GAME_OVER;
                    }
                    break;
                case LV2_DISP:
                    mGameState = LV2_PLAY;
                    mCardManager = new CardManager(this, LV2_CARD_MAX);
                    break;
                case LV2_PLAY:
                    if (mLvTime > LV2_TIME) { //レベル 1 の設定時間を過ぎたら
                        mGameState = GAME_OVER;
                    }
                    break;
                case LV3_DISP:
                    mGameState = LV3_PLAY;
                    mCardManager = new CardManager(this, LV3_CARD_MAX);
                    break;
                case LV3_PLAY:
                    if (mLvTime > LV3_TIME) { //レベル 1 の設定時間を過ぎたら
                        mGameState = GAME_OVER;
                    }
                    break;
                case LV4_DISP:
                    mGameState = LV4_PLAY;
                    mCardManager = new CardManager(this, LV4_CARD_MAX);
                    break;
                case LV4_PLAY:
                    if (mLvTime > LV4_TIME) { //レベル 1 の設定時間を過ぎたら
                        mGameState = GAME_OVER;
                    }

            }
        }// end of switch
        draw();
    } //end of if
}