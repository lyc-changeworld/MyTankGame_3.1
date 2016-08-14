package com.example.achuan.mytank3_0;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;


import java.util.Vector;

/**
 * Created by achuan on 15-12-27.
 */
public class MySurfaceview extends SurfaceView implements View.OnTouchListener,Callback, Runnable {
    //获取屏幕大小，不包括标题
    protected WindowManager wm = (WindowManager)getContext()
            .getSystemService(Context.WINDOW_SERVICE);//载入屏幕大小捕捉对象
    protected int width = wm.getDefaultDisplay().getWidth();//获得屏幕的横向长
    protected int height = wm.getDefaultDisplay().getHeight()-55;//获得屏幕的纵
                                                      // 顶上的时间条占了55高度
    //定义开机声音
    private SoundPool starting_sound;
    //定义图片
    protected Bitmap herotank;//声明我方坦克图片实例
    protected Bitmap enemytank;//声明敌方坦克图片实例
    protected Bitmap home;//声明我方大本营图片实例
    protected Bitmap boom;//声明爆炸效果图片实例
    protected Bitmap hardwall;//声明水泥墙图片实例
    protected Bitmap softwall;//声明木墙图片实例
    protected Bitmap edge;//声明边界图片实例
    protected Bitmap lake;//声明湖水图片实例
    //定义我方打本营
    protected Home basehome=null;
    //定义一个我的坦克
    protected Hero hero=null;
    protected int hero_direct;
    //定义敌人的坦克组
    protected Vector<EnemyTank> ets=new Vector<EnemyTank>();
    protected int ensize=5;//坦克的数目
    //定义炸弹集合
    protected Vector<Bomb>bombs=new Vector<Bomb>();
    //定义水泥墙组
    protected Vector<Hardwall> hws=new Vector<Hardwall>();
    protected int hwsize=10;
    //定义木墙组
    protected Vector<Sofewall> sws=new Vector<Sofewall>();

    protected int angle;//摇杆的移动角度值
    protected  Rocker rocker=null;//创建摇杆实例
    private Thread th;//声明线程
    private SurfaceHolder sfh;//声明一个SurfaceView的抽象接口
    private Canvas canvas;//声明画布
    private Paint paint;//声明画笔
    private boolean flag;//标志进程是否存在
    protected MySurfaceview(Context context)
    {
        super(context);
        //Log.v("achuan", "BeginMySurfaceView");//用来调试代码，同于System.out.println();提示效果
        this.setKeepScreenOn(true);//将屏幕一直保持为开启状态,以便校准
        sfh = this.getHolder();//获取SurfaceHolder实例
        sfh.addCallback(this);//设置生命周期回调接口
        this.setOnTouchListener(this);//设置触屏事件监听
        paint = new Paint();//创建一只画笔
        paint.setAntiAlias(true);//打开抗锯齿
        setFocusable(true);//设置焦点，用来设置键盘是否能获得焦点
        setFocusableInTouchMode(true);//设置焦点，用来设置触摸是否能获得焦点
        //加载声音资源
        starting_sound = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        starting_sound.load(this.getContext(), R.raw.starting_sound, 1);//设置声音id顺序
        //加载图片资源
        herotank= BitmapFactory.decodeResource(this.getResources(), R.drawable.herotank);//加载我方坦克资源
        enemytank= BitmapFactory.decodeResource(this.getResources(), R.drawable.enemytank);//加载敌方坦克图片资源
        home=BitmapFactory.decodeResource(this.getResources(), R.drawable.home);//加载我方大本营资源
        boom=BitmapFactory.decodeResource(this.getResources(), R.drawable.boom);//加载爆炸效果图
        edge=BitmapFactory.decodeResource(this.getResources(), R.drawable.edge);//加载边界效果图
        hardwall=BitmapFactory.decodeResource(this.getResources(), R.drawable.hardwall);//加载水泥墙
        softwall=BitmapFactory.decodeResource(this.getResources(), R.drawable.softwall);//加载木墙
        //System.out.println("图片的长为："+hardwall.getWidth()+"宽为："+hardwall.getHeight());
        //创建一个摇杆实例
        rocker=new Rocker(this.width,this.height);
        //创建我方大本营实例
        basehome=new Home(this.width,this.height);
        //创建我方坦克实例
        hero=new Hero(this.width,this.height,basehome.getX()-60,basehome.getY());
        //初始化敌人的坦克
        for(int i=0;i<ensize;i++)
        {
            //先创建敌人坦克对象
            EnemyTank et=new EnemyTank(this.width,this.height,100+i*200,0);//设置敌方坦克的初始位置
            et.setDirect(2);//设置敌方坦克启动时都朝下
            //加入数组
            ets.add(et);
            //启动线程
            Thread t=new Thread(et);
            t.start();
        }
        //初始化水泥墙
        for (int i=0;i<3;i++)
        {
            Hardwall hw=new Hardwall(this.width,this.height,
                    basehome.getX()-30,this.height-34*(i+1));//设置墙的初始位置
            //加入数组
            hws.add(hw);
        }
        for (int i=0;i<2;i++)
        {
            Hardwall hw=new Hardwall(this.width,this.height,
                    basehome.getX()+i*30,basehome.getY()-34);//设置墙的初始位置
            //加入数组
            hws.add(hw);
        }
        for (int i=0;i<3;i++)
        {
            Hardwall hw=new Hardwall(this.width,this.height,
                    basehome.getX()+60,basehome.getY()+34-34*i);//设置墙的初始位置
            //加入数组
            hws.add(hw);
        }
    }
    public void surfaceCreated(SurfaceHolder holder) {
        th = new Thread(this,"画面线程");//创建一个进程实例
        flag = true;//进程启动，标志为true
        th.start();//启动进程
        starting_sound.play(1, 1, 1, 0, 0, 1);
        //1顺序id,2左声道音量，3右声道音量，4优先级，5（0为循环播放，-1为重复），6播放比率
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action=event.getAction()&MotionEvent.ACTION_MASK;
        int pointerIndex=(event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK)>>
                MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerCount=event.getPointerCount();
        for (int i=0;i<2;i++)//设置两个手指触发事件
        {
            if (i>=pointerCount){
                continue;
            }
            if(event.getAction()!=MotionEvent.ACTION_MOVE&&i!=pointerIndex)
            {
                continue;
            }
            int pointerId=event.getPointerId(i);
            switch (action){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if((float) Math.sqrt(Math.pow(event.getX(i)-width*7/8, 2)//如果手指点击按钮范围
                            + Math.pow(event.getY(i)-rocker.RockerCircleY, 2))<rocker.SmallRockerCircleR)
                    {
                        //判断玩家是否按子弹按钮，开火
                        if(hero.isLive==true)//&&this.hero.ss.size()<10界面中最多产生10颗子弹
                        {
                            this.hero.shotEnemy(hero_direct);//我方坦克活着才可以发射子弹
                        }
                    }
                    if(event.getX(i)<width/4&&event.getY(i)>(height-45)/2)
                    {
                        //得到摇杆与触屏点所形成的角度
                        double tempRad = rocker.getRad(rocker.RockerCircleX, rocker.RockerCircleY, event.getX(), event.getY());
                        angle=(int)(tempRad*180/3.14);//摇杆检测到的角度
                        //System.out.println("rad="+angle);//检测当前摇杆的滑动角度
                        if(angle>-135&&angle<-45)//我方坦克向上移动
                        {
                            this.hero.setDirect(0);
                        }else if(angle>-45&&angle<45)//向右移动
                        {
                            this.hero.setDirect(1);
                        }else if(angle>45&&angle<135)//向下移动
                        {
                            this.hero.setDirect(2);
                        }else if((angle>135&&angle<180)||(angle>-180&&angle<-135))//向左移动
                        {
                            this.hero.setDirect(3);
                        }
                        // 当触屏区域不在活动范围内
                        if (Math.sqrt(Math.pow((rocker.RockerCircleX - (int) event.getX(i)), 2)
                                + Math.pow((rocker.RockerCircleY - (int) event.getY(i)), 2)) >= rocker.RockerCircleR) {
                            //保证内部小圆运动的长度限制
                            rocker.getXY(rocker.RockerCircleX, rocker.RockerCircleY, rocker.RockerCircleR, tempRad);
                        } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
                            rocker.SmallRockerCircleX = (int) event.getX(i);
                            rocker.SmallRockerCircleY = (int) event.getY(i);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                    if(event.getX(i)<width/2&&event.getY(i)>0)//屏幕左半区域放开手指后才复原
                    {
                        //当释放按键时摇杆要恢复摇杆的位置为初始位置
                        rocker.SmallRockerCircleX = rocker.Xtogther;
                        rocker.SmallRockerCircleY = rocker.Ytogther;
                        this.hero.setDirect(4);//停止不动
                    }
                    break;
            }
        }
        return true;
    }
    protected void draw() {//绘图方法
        try {
            switch (hero.direct)
            {
                case 0 :hero.moveUp();break;
                case 1:hero.moveRight();break;
                case 2:hero.moveDown();break;
                case 3:hero.moveLeft();break;
                default:break;
            }
            canvas = sfh.lockCanvas();//获取画布
            canvas.drawColor(Color.BLACK);//设置背景为白色
            //绘制摇杆背景
            rocker.ColorRockerCircle=Color.argb(50, 232, 232, 232);//浅灰色
            paint.setColor(rocker.ColorRockerCircle);
            canvas.drawCircle(rocker.RockerCircleX, rocker.RockerCircleY, rocker.RockerCircleR, paint);
            //绘制摇杆
            rocker.ColorSmallRockerCircl=Color.argb(70, 232, 232, 232);//深灰色
            paint.setColor(rocker.ColorSmallRockerCircl);
            canvas.drawCircle(rocker.SmallRockerCircleX, rocker.SmallRockerCircleY,//绘制摇杆
                    rocker.SmallRockerCircleR, paint);
            //绘制子弹按钮
            canvas.drawCircle(width * 7 / 8, rocker.RockerCircleY, rocker.SmallRockerCircleR, paint);
            //绘制游戏边框
            paint.setColor(Color.argb(255, 139, 69, 47));//设置画笔颜色为棕色
            canvas.drawRect(0, 0, 5, height, paint);//左边框
            canvas.drawRect(width - 5, 0, width, height, paint);//右边框
            canvas.drawRect(0, height-5, width, height, paint);//底边框
            canvas.drawRect(0,0,width,5,paint);//上边框
            //测试框架范围
            //canvas.drawRect(width/2, 0, width/2+5, height, paint);//横向1/2
            //canvas.drawRect(0,height/2,width,height/2+5,paint);//纵向1/2
            //canvas.drawRect(width/4, height/2, width/4+5, height, paint);//横向1/4
            //测试子弹按钮范围
            //canvas.drawRect(width*7/8, height/2, width*7/8+5, height, paint);
            //画出我方的子弹，从我方的ss中取出每一颗子弹，并画出
            if(hero.isLive==true)//我方坦克活着才可以发射子弹
            {
                for(int i=0;i<hero.ss.size();i++)
                {
                    Shot myshot=hero.ss.get(i);
                    //画出我方的子弹
                    if(myshot!=null&&myshot.isLive==true)
                    {
                        paint.setColor(Color.argb(255, 0, 255, 0));//亮绿色子弹
                        canvas.drawCircle(myshot.x,myshot. y, 5, paint);
                    }
                    if(myshot.isLive==false)
                    {
                        //从向量中删除该子弹
                        hero.ss.remove(myshot);
                    }
                }
            }
            //画出炸弹
            for(int i=0;i<bombs.size();i++)
            {
                Bomb b=bombs.get(i);
                if(b.life>0)
                {
                    canvas.drawBitmap(boom, b.x, b.y, paint);
                }
                //让b的生命值减小
                b.lifeDown();
                //如果炸弹生命值为0，就让炸弹从bombs向量中移除
                if(b.life==0)
                {
                    bombs.remove(b);
                }
            }
            //画出水泥墙
            for (int i=0;i<hws.size();i++)
            {
                Hardwall hw=hws.get(i);
                if(hw.isLive)
                {
                    canvas.drawBitmap(hardwall,hw.getX(),hw.getY(),paint);
                }
            }
            /****************绘制主要对象的图片****************/
            canvas.drawBitmap(zoomImage(home,60,68), basehome.getX(),basehome.getY(), paint);//绘制我方大本营
            if(hero.isLive==true)//绘制我方坦克
            {
                if(hero.direct<4)//绘制我方坦克图片
                {
                    hero_direct=hero.direct;//保持上次的方向
                    canvas.drawBitmap(SetDirect(herotank, hero.direct), hero.getX(), hero.getY(), paint);
                }
                else {
                    canvas.drawBitmap(SetDirect(herotank,hero_direct), hero.getX(), hero.getY(), paint);
                }
            }
            //绘制敌方的坦克
            for(int i=0;i<ets.size();i++)
            {
                EnemyTank et=ets.get(i);
                if(et.isLive)//敌方的坦克活着才可以发射子弹
                {
                    canvas.drawBitmap(SetDirect(enemytank,et.getDirect()),
                            et.getX(),et.getY() , paint);//绘制敌方坦克
                    //再画出敌人的子弹
                    for(int j=0;j<et.ss.size();j++)
                    {
                        //取出子弹
                        Shot enemyShot=et.ss.get(j);
                        if(enemyShot.isLive)
                        {
                            paint.setColor(Color.argb(255, 255, 0, 0));//亮红色子弹
                            canvas.drawCircle(enemyShot.x,enemyShot.y, 5, paint);
                        }
                        else {
                            //如果敌人坦克死亡就从Vector去除
                            et.ss.remove(enemyShot);
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();//打印异常
        } finally {
            try {
                if (canvas != null)//判断canvas是否为空
                    sfh.unlockCanvasAndPost(canvas);//解锁
            } catch (Exception e2) {
                e2.printStackTrace();//打印异常
            }
        }
    }
    //判断我方坦克是否需要复活
    public void MyTankRlive()
    {
        if(hero.getBoold()>0&&hero.isLive==false)
        {
            hero.isLive=true;//血量够的话，我方坦克就可以复活
            hero.setX(basehome.getX()-60);
            hero.setY(basehome.getY());
        }
    }
    //判断敌方坦克的子弹是否击中我的坦克
    public void hitMe()
    {
        //取出每个敌人的坦克
        for(int i=0;i<this.ets.size();i++)
        {
            //取出坦克
            EnemyTank et=ets.get(i);
            //取出每一颗子弹
            for(int j=0;j<et.ss.size();j++)
            {
                Shot enemyShot=et.ss.get(j);
                if(hero.isLive==true)
                {
                    this.hitTank(enemyShot, hero);
                }
            }
        }
    }
    //判断我的子弹是否击中敌人的坦克
    public void hitEnemyTank()
    {
        //判断是否击敌人的坦克
        for(int i=0;i<hero.ss.size();i++)
        {
            //取出子弹
            Shot myshot=hero.ss.get(i);
            //判断子弹是否有效
            if(myshot.isLive)
            {
                //取出每个敌人坦克，与之判断
                for(int j=0;j<ets.size();j++)
                {
                    //取出坦克
                    EnemyTank et=ets.get(j);
                    if(et.isLive)
                    {
                        this.hitTank(myshot, et);
                    }
                }
            }
        }
    }
    //写一个函数判断子弹是否击中坦克,产生爆炸效果
    public void hitTank(Shot s,Tank et)
    {
        if(s.x>et.x&&s.x<et.x+60&&s.y>et.y&&s.y<et.y+60)
        {
                    //击中
                    //子弹死亡
                    s.isLive=false;
            if(et==hero&&hero.getBoold()>0)//击中我方坦克时
            {
                hero.setBoold(hero.getBoold()-1);//血量减1
            }
            et.isLive=false;//坦克被击中就死亡
            //坦克死亡
            et.isLive=false;
            //创建一颗炸弹，放入Vector
            Bomb b=new Bomb(et.x,et.y);
            bombs.add(b);
        }
    }
    public void run() {//进程工作函数,一旦启动后就一直工作，除非销毁进程
        System.out.println(Thread.currentThread().getName());
        // TODO Auto-generated method stubt
        while (flag) {
            draw();
            try {
                Thread.sleep(50);//进程以50ms的周期工作
            } catch (Exception e3) {
                e3.printStackTrace();//打印异常
            }
            //判断我方坦克的子弹是否击中了敌人坦克
            this.hitEnemyTank();
            //判断敌人的子弹是否击中我了
            this.hitMe();
            //判断我方坦克是否可以复活
            this.MyTankRlive();
        }
    }
    /**********××××××××*****实现图片的缩放或放大**********************/
    public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
        int width = bgimage.getWidth();
        int height= bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix1 = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix1.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                matrix1, true);
        return bitmap;
    }
    /*************实现图片旋转方法封装**************/
    protected Bitmap SetDirect(Bitmap picture,int rotation)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(90*rotation);//翻转的角度
        int width =picture.getWidth();
        int height=picture.getHeight();
        return Bitmap.createBitmap(picture, 0, 0, width, height, matrix, true);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;//界面被销毁的同时，进程也将停止
    }
}
