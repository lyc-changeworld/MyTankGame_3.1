package com.example.achuan.mytank3_0;

/**
 * Created by achuan on 16-1-9.
 */
public class Scene {
}
//父类墙
class Wall
{
    //表示墙的横坐标
    protected int x=0;
    //纵坐标
    protected int y=0;

    protected  boolean isLive=true;
    //界面的宽和高
    protected int width;
    protected  int height;
    protected  Wall(int width,int height,int x,int y)//父亲墙构造器
    {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
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
//水泥墙
class Hardwall extends Wall//30*34
{
    protected Hardwall(int width, int height, int x, int y)
    {
        super(width, height, x, y);
    }
}
//木墙
class Sofewall extends Wall
{

    protected Sofewall(int width, int height, int x, int y)
    {
        super(width, height, x, y);
    }
}
