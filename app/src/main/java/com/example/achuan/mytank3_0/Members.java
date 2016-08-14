package com.example.achuan.mytank3_0;

import java.util.Vector;

/**
 * Created by achuan on 15-12-27.
 * 功能：实现坦克类
 */
public class Members {

}
//炸弹类
class Bomb
{
    //定义炸弹的坐标
    int x,y;
    //炸弹的生命
    int life=5;
    boolean isLive=true;
    public Bomb(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
    //减少生命值
    public void lifeDown()
    {
        if(life>0)
        {
            life--;
        }else{
            this.isLive=false;
        }
    }
}
//定义子弹类
class Shot implements Runnable
{
    protected int x;
    protected int y;
    protected int direct;
    protected int speed=20;
    private Thread th1;//声明线程
    protected boolean isLive=true;//判断子弹是否死亡
    protected int width;
    protected  int height;//界面的长和宽

    protected Shot(int x, int y,int direct,int width,int height)
    {
        this.x=x;
        this.y=y;
        this.direct=direct;
        this.width=width;
        this.height=height;
    }
    @Override
    public void run() {
        while(true)
        {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            switch(this.direct)
            {
                case 0://向上发射子弹
                    y-=speed;
                    break;
                case 1://向右发射子弹
                    x+=speed;
                    break;
                case 2://向下发射子弹
                    y+=speed;
                    break;
                case 3://向左发射子弹
                    x-=speed;
                    break;
            }
            //子弹何时死亡？？?
            //判断该子弹是否碰到边缘
            if(x<5||x>this.width-5||y<5||y>this.height-5)
            {
                this.isLive=false;
                break;
            }
        }
    }
}
//定义坦克类
class Tank//坦克的长宽均为60
{
    //表示坦克的横坐标
    protected int x=0;
    //坦克纵坐标
    protected int y=0;

    //坦克方向
    //0表示上 1表示右 2表示下 3表示左 4表示停止
    protected  int direct=4;//默认坦克创建时静止不动

    protected  boolean isLive=true;
    protected  int boold;
    //坦克的速度
    protected int speed=5;
    //界面的宽和高
    protected int width;
    protected  int height;

    protected Tank(int width,int height,int x,int y)//父类坦克构造器
    {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    protected int getBoold()
    {
        return boold;
    }
    protected void setBoold(int boold)
    {
        this.boold=boold;
    }
    protected int getSpeed() {
        return speed;
    }
    protected void setSpeed(int speed) {
        this.speed = speed;
    }
    protected int getDirect() {
        return direct;
    }
    protected void setDirect(int direct) {
        this.direct = direct;
    }
    protected int getX() {
        return x;
    }
    protected void setX(int x) {
        this.x = x;
    }
    protected int getY() {
        return y;
    }
    protected void setY(int y) {
        this.y = y;
    }
}

//敌人坦克类
class EnemyTank extends Tank implements Runnable
{
    private int step=30;//敌方坦克持续向一个方向(回合)走的步子数
    int time=0;
    //定义一个向量，可以存放敌人的子弹
    Vector<Shot> ss=new Vector<Shot>();
    //敌人添加子弹，应当在刚刚创建坦克和敌人的坦克死亡后
    protected EnemyTank(int width,int height,int x,int y) {//敌方坦克构造器
        super(width,height,x,y);
    }
    public void run() {
        while(true)
        {
            switch(this.direct)//坦克向一个方向持续的时间越久，越好击中
            {
                case 0:
                    //说明坦克在向上
                    for(int i=0;i<step;i++)//设置敌方坦克向一个方向持续走step步，为step*50(ms)
                    {
                        if(y>0+5)
                        {
                            y-=speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    //坦克在向右
                    for(int i=0;i<step;i++)
                    {
                        if(x<this.width-60-5)
                        {
                            x+=speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    //坦克在向下
                    for(int i=0;i<step;i++)
                    {
                        if(y<this.height-60-5)
                        {
                            y+=speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    //坦克在向左
                    for(int i=0;i<step;i++)
                    {
                        if(x>0+5)
                        {
                            x-=speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            this.time++;
            if(time%2==0)//敌方坦克走两个回合就发射一颗子弹
            {
                if(isLive)//坦克活着才可以发射子弹
                {
                    if(this.ss.size()<5)//每辆敌方坦克在界面中最多存活5颗子弹
                    {
                        //判断是否需要给坦克加入新的子弹
                        Shot s=null;
                        //没有子弹
                        //添加
                        switch(this.direct)
                        {
                            case 0:
                                //创建一颗子弹
                                s=new Shot(x+30,y,0,this.width,this.height);
                                //把子弹加入到向量
                                ss.add(s);
                                break;
                            case 1:
                                s=new Shot(x+60,y+30,1,this.width,this.height);
                                ss.add(s);
                                break;
                            case 2:
                                s=new Shot(x+30,y+60,2,this.width,this.height);
                                ss.add(s);
                                break;
                            case 3:
                                s=new Shot(x,y+30,3,this.width,this.height);
                                ss.add(s);
                                break;
                        }
                        //启动子弹线程
                        Thread t=new Thread(s);
                        t.start();
                    }
                }
            }
            //让坦克随机产生一个新的方向
            this.direct=(int)(Math.random()*4);
            //判断敌人坦克是否是否死亡
            if(this.isLive==false)
            {
                //让坦克死亡后，退出线程
                break;
            }
        }
    }
}

//我的坦克
class Hero extends Tank
{
    //子弹
    protected Shot s=null;
    //子弹连发设置，集合类使用，线程安全
    protected  Vector<Shot> ss=new Vector<Shot>();

    //开火
    protected void shotEnemy(int d)
    {
        switch(d)
        {
            case 0://向上发射子弹
                s=new Shot(x+30,y,0,this.width,this.height);
                //把子弹加入到向量
                ss.add(s);
                break;
            case 1://向右发射子弹
                s=new Shot(x+60,y+30,1,this.width,this.height);
                ss.add(s);
                break;
            case 2://向下发射子弹
                s=new Shot(x+30,y+60,2,this.width,this.height);
                ss.add(s);
                break;
            case 3://向左发射子弹
                s=new Shot(x,y+30,3,this.width,this.height);
                ss.add(s);
                break;
        }
        //启动子弹线程
        Thread t1=new Thread(s);
        t1.start();
    }
    protected  Hero(int width,int height,int x,int y)//我方坦克构造器
    {
        super(width, height, x, y);
        this.setSpeed(speed * 2);//设置我方坦克速度比基本坦克快一倍
        this.setBoold(10);//我方坦克拥有的最大血量为10
    }
    //坦克向的移动 上、右、下、左
    protected void moveUp()//向上
    {
        if(y>0+5)//+5是因为加了5宽度的边框1
            y-=this.speed;
    }
    protected void moveRight()//向右
    {
        if(x<this.width-60-5)//-60恰好//-5是因为加了5宽度的边框
            x+=this.speed;

    }
    protected void moveDown()//向下
    {
        if(y<this.height-60-5)//
            y+=this.speed;
    }
    protected void moveLeft()//向左
    {
        if(x>0+5)//+5是因为加了5宽度的边框
            x-=this.speed;
    }
}
class Home//大本营的长宽均为80
{
    //表示大本营的横坐标
    protected int x=0;
    //大本营的纵坐标
    protected int y=0;
    //标记大本营是否存活
    protected  boolean isLive=true;
    //界面的长和宽
    //protected int width;
    //protected int height;
    protected Home(int width,int height)//大本营构造器
    {
        this.x=width/2-35;
        this.y=height-68;
    }
    protected int getX()
    {
        return x;
    }
    protected int getY()
    {
        return y;
    }
    protected void setX(int x)
    {
        this.x=x;
    }
    protected void setY(int y)
    {
        this.y=y;
    }
}