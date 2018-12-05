package com.aisino.trusthandwrite.custom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by HXQ on 2017/5/4.
 */

public class BaseDragZoomImageView extends ImageView implements View.OnTouchListener{


    /** 用于记录图片缩放的大小 */
    private Matrix matrix = new Matrix();

    /** 记录上一次单个手指的坐标点 */
    private PointF lastPoint;
    /** 两个手指的中间点 */
    private PointF midPoint;

    /** 记录上次两指之间的距离 */
    private float lastFingerDis;
    /** 记录图片的总缩放比例 */
    private float totalRatio = 1;
    /** 记录图片允许的最大缩放比例 */
    private static final float MAX_RATIO = 2;
    /** 记录图片允许的最小缩放比例 */
    private static final float MIN_RATIO = 1;

    /** 原点*/
    private float originX = 0;
    private float originY = 0;

    /**
     * 记录图片此时左上，右上，左下，右下四个顶点的坐标
     */
    private PointF imageLeftTopPoint;
    private PointF imageRightTopPoint;
    private PointF imageLeftBottomPoint;
    private PointF imageRightBottomPoint;


    public BaseDragZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    public BaseDragZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setOnTouchListener(this);
    }

    public BaseDragZoomImageView(Context context) {
        this(context, null);
        setOnTouchListener(this);
    }

    /** 计算两个手指间的距离 */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /** 计算两个手指间的中间点 */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    /**
     * 缩放操作
     * @param scale 缩放倍数
     */
    private void zoom(float scale){
        //计算总的缩放倍数
        totalRatio = totalRatio * scale;

        matrix.set(getImageMatrix());
        //进行缩放
        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
    }

    /**
     * 对图片的缩放进行阈值判断，如超过，则回弹至规定极限值
     */
    private void zoomLimit(){
        if (totalRatio > MAX_RATIO){//缩放倍数超出规定最大放大值，则缩小为规定最大值
            // 得到此次缩放倍数
            float scale = MAX_RATIO / totalRatio;
            //自动缩放
            zoom(scale);
        }else if (totalRatio < MIN_RATIO){//缩放倍数超出规定最小缩小值，则放大为规定最小值
            // 得到此次缩放倍数
            float scale = MIN_RATIO / totalRatio;
            //自动缩放
            zoom(scale);
        }
    }

    /**
     * 获取当前图片的四个顶点的坐标
     */
    private void getImagePoint(){
        //获取图片此时的左上顶点的坐标
        RectF r = new RectF();
        matrix.mapRect(r);
        float leftTopX = r.left;
        float leftTopY = r.top;
        imageLeftTopPoint = new PointF(leftTopX, leftTopY);
        //计算图片此时宽高
        float width = getWidth() * totalRatio + leftTopX;
        float height = getHeight() * totalRatio + leftTopY;
        //计算图片此时右上的坐标
        float rightTopX = width;
        float rightTopY = leftTopY;
        imageRightTopPoint = new PointF(rightTopX, rightTopY);
        //计算图片此时左下的坐标
        float leftBottomX = leftTopX;
        float leftBottomY = height;
        imageLeftBottomPoint = new PointF(leftBottomX, leftBottomY);
        //计算图片此时右右下的坐标
        float rightBottomX = width;
        float rightBottomY = height;
        imageRightBottomPoint = new PointF(rightBottomX, rightBottomY);
    }

    /**
     * 判断图片是否偏移，如偏移进行复位
     */
    private void translateImageView(){
        //获取此时图片的顶点坐标,进行图片位置判断
        getImagePoint();
        float translateX = 0f;
        float translateY = 0f;
        float imageViewWidth = getWidth();
        float imageViewHeight = getHeight();
        //进行偏移判断
        if (imageLeftTopPoint.x > originX){
            translateX = originX - imageLeftTopPoint.x;
        }else if (imageRightTopPoint.x < imageViewWidth){
            translateX = imageViewWidth - imageRightTopPoint.x;
        }
        if (imageLeftTopPoint.y > originY){
            translateY = originY - imageLeftTopPoint.y;
        }else if (imageLeftBottomPoint.y < imageViewHeight){
            translateY = imageViewHeight - imageLeftBottomPoint.y;
        }

        //如果有偏移，进行复位操作
        if (translateX != 0 || translateY != 0){
            matrix.postTranslate(translateX, translateY);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN:
                //只有一根手指时记录坐标
                if (event.getPointerCount() == 1){
                    //getImagePoint();
                    //记录单指的坐标
                    lastPoint = new PointF(event.getX(), event.getY());
                }
                break;
            // 手指在屏幕上移动，改事件会被不断触发
            case MotionEvent.ACTION_MOVE:
                //一根手指在触摸屏上时
                if (event.getPointerCount() == 1){
                    float dx = event.getX() - lastPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - lastPoint.y; // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    matrix.postTranslate(dx/2, dy/2);
                    //记录单指的坐标
                    lastPoint = new PointF(event.getX(), event.getY());
                }
                //两根手指在触摸屏上时
                if (event.getPointerCount() == 2) {
                    float fingerDis = distance(event);
                    // 得到此次缩放倍数
                    float scale = fingerDis / lastFingerDis;
                    //缩放
                    zoom(scale);
                    //Log.i("zoom end","width "+ getWidth() +" height"+getHeight());
                    //记录此时的手指距离
                    lastFingerDis = fingerDis;
                }
                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
                //判断图片是否偏移，如偏移进行复位
                translateImageView();
                break;
            // 当触点离开屏幕，但是屏幕上还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                //只有一根手指时记录坐标
                if (event.getPointerCount() == 1){
                    //记录单指的坐标
                    lastPoint = new PointF(event.getX(), event.getY());
                }
                //缩放结束，进行缩放阈值判断
                zoomLimit();
                //判断图片是否偏移，如偏移进行复位
                translateImageView();
                break;
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                //有两根手指在触摸屏上
                if (event.getPointerCount() == 2) {
                    // 计算两指之间的距离
                    lastFingerDis = distance(event);
                    //获取中间点坐标
                    midPoint = mid(event);
                }
                break;
        }
        //将新的图片的相关值覆盖旧的
        setImageMatrix(matrix);
        return true;
    }
}
