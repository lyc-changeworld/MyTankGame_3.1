package com.example.achuan.mytank3_0;

/**
 * Created by achuan on 15-12-27.
 * 功能：实现摇杆检测手指的移动弧度
 */
public class RockerCircle {

}

class Rocker
{
    protected int Xtogther;
    protected  int Ytogther;
    //固定摇杆背景圆形的X,Y坐标以及半径
    protected int RockerCircleX;
    protected int RockerCircleY;
    protected int RockerCircleR = 120;
    //摇杆的X,Y坐标以及摇杆的半径
    protected float SmallRockerCircleX;
    protected float SmallRockerCircleY;
    protected float SmallRockerCircleR = RockerCircleR/2;
    //颜色分配
    protected int ColorRockerCircle;
    protected int ColorSmallRockerCircl;

    protected  Rocker(int WindowWidth,int WindowHeight)
    {
        this.Xtogther=(int)(WindowWidth/8);
        this.Ytogther=(int)(WindowHeight*3/4);
        RockerCircleX = Xtogther;
        RockerCircleY = Ytogther;//摇杆背景的位置确定
        SmallRockerCircleX = Xtogther;
        SmallRockerCircleY = Ytogther;//摇杆的位置确定
    }
    /***
     * 得到手指滑动后两点之间的弧度
     */
    protected double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }
    /**
     *
     * @param R
     *            手指滑动后，圆周运动的旋转点
     * @param centerX
     *            旋转点X
     * @param centerY
     *            旋转点Y
     * @param rad
     *            旋转的弧度
     */
    protected void getXY(float centerX, float centerY, float R, double rad) {
        //System.out.println("rad="+rad*180/3.14);
        //获取圆周运动的X坐标
        SmallRockerCircleX = (float) (R * Math.cos(rad)) + centerX;
        //获取圆周运动的Y坐标
        SmallRockerCircleY = (float) (R * Math.sin(rad)) + centerY;
    }
}
