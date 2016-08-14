/*坦克大战3.1
功能：1、实现摇杆控制我方坦克移动,并且我方坦克可以发射子弹，而且限制发射子弹的数量（摇杆不放时，可以发射子弹）；
     2、载入可以移动的敌方坦克（有重叠现象）；
     3、敌方坦克可以发射子弹，而且子弹击中坦克会出现爆炸效果；（我方坦克有多条命，普通的敌方坦克只有一条）
     4、加载了水泥墙元素
 */
package com.example.achuan.mytank3_0;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        MySurfaceview mySurfaceview=new MySurfaceview(this);
        this.setContentView(mySurfaceview);
    }
}
/*<resources>
    <!-- Base application theme. -->
    <style name="FullScreenTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>
*/
