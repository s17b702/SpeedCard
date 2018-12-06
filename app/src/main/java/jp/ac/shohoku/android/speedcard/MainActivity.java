/**
 * Copyright (c) 2018 Android開発 カードプロジェクト
 * Licensed under the Apache License, Version 2.0 (the “Lisence”);
 * you may not use this file except in compliance with the License.
 * you may obtaion a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ac.shohoku.android.speedcard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    public static int width = 0;
    public static int height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override

    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        // View のサイズを取得
        SpeedCardView sview = (SpeedCardView) findViewById(R.id.SpeedCardView);
        width = sview.getWidth();
        height = sview.getHeight();
    }

    /**
     * SpeedCardView の幅を取得
     * @return
     */
    public static int getViewWidth(){
        return width;
    }

    /**
     * SpeedCardView の高さを取得
     * @return
     */
    public static int getViewHeight() {
        return height;
    }
}