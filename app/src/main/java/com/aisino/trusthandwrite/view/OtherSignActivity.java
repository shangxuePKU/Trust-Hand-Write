package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aisino.trusthandwrite.R;
import com.aisino.trusthandwrite.custom.BaseDragZoomImageView;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.model.Contract;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.model.StaticSignPos;
import com.aisino.trusthandwrite.util.Base64Util;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;
import com.aisino.trusthandwrite.util.PackageUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HXQ on 2017/6/2.
 */

public class OtherSignActivity extends Activity {

    //startActivityForResult跳转时用于区分的值
    private final int REQUESTCODE_HANDSIGN = 1;
    private final int REQUESTCODE_INFOCAPTURE = 2;
    /** ContractImageView默认的宽高，就是父控件的宽高 */
    private int imageDefalutWidth;
    private int imageDefalutHeight;
    /** 滑动进度条值的倍数值 */
    private int seekBarScale;
    /** 界面控件*/
    private BaseDragZoomImageView contractImageView;
    private Button signImageButton;
    private Button signStaticImageButton;
    private ImageView signImageView;
    private TextView pageTextView;
    private SeekBar seekBar;
    TopNavigationView topNavigationView;

    /** 记录上一次单个手指的坐标点 */
    private PointF lastPoint;
    /** 签章图片可以移动的上下左右的界限*/
    private float leftLimit;
    private float topLimit;
    private float rightLimit;
    private float bottomLimit;

    /** 记录手签图片在合同图片中的坐标 */
    private int signImageLeft;
    private int signImageTop;
    /** 记录手签图片的宽高 */
    private int signImageWidth;
    private int signImageHeight;
    /** 记录合同图片的实际宽高 */
    private int pageWidth;
    private int pageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_contract);

        //设置默认值
        setDefalut();
        //查询合同数据
        setDefalutData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求码来区别
        switch (requestCode) {
            case REQUESTCODE_HANDSIGN:
                setSignatureBitmap();
                break;
            case REQUESTCODE_INFOCAPTURE:
                readySign();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            backOtherApp(true);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 设置手写签名图片和隐藏其余按钮
     */
    private void setSignatureBitmap(){
        //从手写签名页面回来
        if (DataModel.signatureBitmap != null) {
            //显示手写签名
            adaptSignImageView();
            signImageView.setImageBitmap(DataModel.signatureBitmap);
            signImageView.setVisibility(View.VISIBLE);
            //隐藏去手写签名界面按钮,判断是否固定签章
            if (DataModel.otherData.isStaticPOS()){//是固定签署
                signStaticImageButton.setVisibility(View.GONE);
            }else {//不是固定签署
                signImageButton.setVisibility(View.GONE);
            }
            //显示下一步按钮
            topNavigationView.showNextBtn();
        }
    }

    /**
     * 设置默认值
     */
    private void setDefalut(){
        //设置导航栏
        topNavigationView = (TopNavigationView)findViewById(R.id.sc_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.OTHERSIGN_ACTIVITY);
        topNavigationView.setNavigationOtherBackClickListner(new TopNavigationView.NavigationOtherBackClickListner() {
            @Override
            public void back() {
                backOtherApp(true);
            }
        });
        topNavigationView.setNavigationClickListner(new TopNavigationView.NavigationClickListner() {
            @Override
            public void next() {
                nextBtnClick();
            }
        });

        contractImageView = (BaseDragZoomImageView)this.findViewById(R.id.mid_Iv);
        signImageButton = (Button)this.findViewById(R.id.mid_signImageBtn);
        signStaticImageButton = (Button)this.findViewById(R.id.mid_signStaticImageBtn) ;
        signImageView = (ImageView)this.findViewById(R.id.mid_signImageView);
        pageTextView = (TextView)this.findViewById(R.id.mid_Tv);
        seekBar = (SeekBar)this.findViewById(R.id.mid_SeekBar);
        //设置观察者，在contractImageView绘制前获取控件真实宽和高
        ViewTreeObserver vto = contractImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contractImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageDefalutWidth = contractImageView.getWidth();
                imageDefalutHeight = contractImageView.getHeight();
            }
        });
    }

    /**
     * 设置默认数据
     */
    private void setDefalutData(){
        //显示等待动画
        DialogUtil.showWaitingDialog(OtherSignActivity.this,
                Message.getMessage(Message.Message_Contract_Get_Loading));
        //请求合同数据
        getContract();
    }

    /**
     * 封装参数，发起请求合同请求
     */
    private void getContract(){
        // 使用Map封装请求参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", DataModel.otherData.getCode());
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_GetContractByCode);
        // 发送请求
        HttpUtil.postRequestTest(url, json, false, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result) {
                //判断请求是否成功
                if (status) {//网络请求成功
                    //解析json获取数据
                    Type type = new TypeToken<Response<Contract>>(){}.getType();
                    Response response = GsonUtil.GsonToBean(result, type);
                    //对请求合同操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功
                        Contract contract = (Contract)response.getDatas();

                        DataModel.otherData.setPages(contract.getPages());
                        //判断是否有固定签章位置
                        if (contract.getStaticSignPos() != null){//有固定签章位置
                            DataModel.otherData.setStaticPOS(true);
                            DataModel.otherData.setStaticSignPos(contract.getStaticSignPos());
                        }else {//没有
                            DataModel.otherData.setStaticPOS(false);
                            //设置签章图片的拖拽响应事件
                            setSignImageViewDrag();
                        }
                        //设置页码信息和滑动进度条
                        setTextViewValue();
                        setSeekBarValue();
                        //请求图片
                        for (int i = 1; i <= contract.getPages(); i++){
                            getContractImage(i);
                        }
                    } else {//操作失败
                        DialogUtil.hiddenWaitingDialog();
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(OtherSignActivity.this, message, null);
                    }
                }else {//网络请求失败
                    DialogUtil.hiddenWaitingDialog();
                    DialogUtil.showSubmitDialog(OtherSignActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 获取合同图片
     * @param page 请求的图片所在页码
     */
    private void getContractImage(final int page){
        // 使用Map封装请求参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", DataModel.otherData.getCode());
        map.put("id", page);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_GetImageByCode);
        // 发送请求
        HttpUtil.postRequestTest(url, json, false, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result) {
                //判断请求是否成功
                if (status){//网络请求成功
                    //解析json获取数据
                    Type type = new TypeToken<Response<Map<String, String>>>(){}.getType();
                    Response response = GsonUtil.GsonToBean(result, type);
                    //对请求合同图片操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功
                        //取图片数据
                        String base64Data = ((Map<String, String>)response.getDatas()).get("image");
                        Bitmap bitmap = Base64Util.base64ToBitmap(base64Data);
                        //图片放到缓存中
                        DataModel.otherData.getImages().addBitmapToCache(page, bitmap);
                        //请求到第一张图片则隐藏等待框
                        if (page == 1){
                            DialogUtil.hiddenWaitingDialog();
                        }
                        //DataModel.otherData.getPage()默认值为1，但是也可能因为用户滑动SeekBar改变
                        if (page == DataModel.otherData.getPage()){
                            setContractImageViewValue();
                        }
                    }else {//操作失败
                        DialogUtil.hiddenWaitingDialog();
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(OtherSignActivity.this, message, null);
                    }
                }else {//网络请求失败
                    DialogUtil.hiddenWaitingDialog();
                    DialogUtil.showSubmitDialog(OtherSignActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 设置当前页数与总页数的文本信息
     */
    private void setTextViewValue(){
        pageTextView.setText("当前浏览页数" + DataModel.otherData.getPage() + "/" + DataModel.otherData.getPages());
    }

    /**
     * 设置滑动进度条最大值，和监听滑动事件,因为seekBar设置最大值太小，滑动体验很差，如果合同页数小于30，则设置最大值为总页数*倍数>50
     */
    private void setSeekBarValue(){
        //seekBar最小值为0，而page最小值为1
        int max = 0, pages;
        pages = DataModel.otherData.getPages();
        //因为seekBar设置最大值太小，滑动体验很差，如果合同页数小于30，则设置最大值为总页数*倍数>=50
        if (0 < pages && pages < 30){
            max = (100 % pages == 0)? 100 : ((100 / pages + 1) * pages);
            seekBarScale = max / pages;
        }else {
            max = pages;
            seekBarScale = 1;
        }
        seekBar.setMax(max - 1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int page = (i/seekBarScale) + 1;
                if (page != DataModel.otherData.getPage()){
                    DataModel.otherData.setPage(page);
                    pageTextView.setText("当前浏览页数" + DataModel.otherData.getPage() + "/" + DataModel.otherData.getPages());
                    setContractImageViewValue();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 设置当前显示的合同图片
     */
    private void setContractImageViewValue(){
        Bitmap bitmap = DataModel.otherData.getImages().getBitmapFromCache(DataModel.otherData.getPage());
        if (bitmap != null){
            //改变bitmap大小适应控件大小
            Bitmap zoomBitmap = adaptContractImageView(bitmap);
            //改变去签署页面的按钮的位置
            contractImageView.setImageBitmap(zoomBitmap);
        }else {
            contractImageView.setImageBitmap(null);
        }
        getDragLimit();
        //是否固定签章
        if (DataModel.otherData.isStaticPOS()){//是固定签署
            adaptStaticImageButton();
        }else {//不是固定签署
            adaptImageButton();
        }
    }

    /**
     *  处理图片,改变bitmap大小适应ContractImageView大小
     * @param bm 所要转换的bitmap
     * @return 指定宽高的bitmap
     */
    private Bitmap adaptContractImageView(Bitmap bm){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width == imageDefalutWidth && height == imageDefalutHeight){
            return bm;
        }else {
            // 计算缩放比例
            float scaleWidth = ((float) imageDefalutWidth) / width;
            float scaleHeight = ((float) imageDefalutHeight) / height;
            float scale = (scaleWidth > scaleHeight)? scaleHeight: scaleWidth;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            //根据图片大小改变imageView大小
            int newImageWidth = (int)(width * scale + 0.5f);
            int newImageHeight = (int)(height * scale + 0.5f);
            //获取imageView布局
            ViewGroup.LayoutParams layoutParams  = contractImageView.getLayoutParams();
            layoutParams .width = newImageWidth;
            layoutParams .height = newImageHeight;
            //使设置好的布局参数应用到控件
            contractImageView.setLayoutParams(layoutParams );
            return newbm;
        }
    }

    /**
     * 得到可拖动的边界,因为ContractImageView通过设置layout_centerHorizontal和layout_centerVertical居中
     * 所以导致ContractImageView与父控件有边距，但是获取边距为0
     */
    private void getDragLimit(){
        //根据imageView的坐标和宽高获取边界
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams)contractImageView.getLayoutParams();
        //layoutParams.width为改变后的宽度，contractImageView.getRight()为改变前的宽度
        leftLimit = (contractImageView.getRight() - layoutParams.width)/2;
        rightLimit = leftLimit + layoutParams.width;
        //layoutParams.height为改变后的高度，contractImageView.getBottom()为改变前的高度
        topLimit = (contractImageView.getBottom() - layoutParams.height)/2;
        bottomLimit = topLimit + layoutParams.height;
    }

    /**
     * 根据ContractImageView变动改变去签署页面的按钮的位置
     */
    private void adaptImageButton(){
        //获取signImageButton的布局
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams)signImageButton.getLayoutParams();
        //根据边界将signImageButton设置在边界的最右上位置
        layoutParams.leftMargin = (int)(rightLimit + 0.5f) - layoutParams.width - 20;
        layoutParams.topMargin = (int)(topLimit + 0.5f) + 20;
        signImageButton.setLayoutParams(layoutParams);
        signImageButton.setVisibility(View.VISIBLE);
    }

    /**
     * 固定签署情况下，根据ContractImageView变动改变去签署页面的按钮的位置
     */
    private void adaptStaticImageButton(){
        //获取signStaticImageButton的布局
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams)signStaticImageButton.getLayoutParams();
        //获取imageView的布局
        RelativeLayout.LayoutParams contractLayoutParams  = (RelativeLayout.LayoutParams)contractImageView.getLayoutParams();
        //计算数据端固定签章与合同宽高比例
        StaticSignPos staticSignPos = DataModel.otherData.getStaticSignPos();
        float widthScale = staticSignPos.getWidth() / staticSignPos.getPageWidth();
        float heightScale = staticSignPos.getHeight() / staticSignPos.getPageHeight();
        //计算signStaticImageButton宽和高
        layoutParams.width = (int)((float)contractLayoutParams.width * widthScale + 0.5f);
        layoutParams.height = (int)((float)contractLayoutParams.height * heightScale + 0.5f);
        //计算signStaticImageButton的位置
        float leftScale = staticSignPos.getLeft() / staticSignPos.getPageWidth();
        float topScale = staticSignPos.getTop() / staticSignPos.getPageHeight();
        layoutParams.leftMargin = (int)(((float)contractLayoutParams.width * leftScale) + leftLimit + 0.5f);
        layoutParams.topMargin = (int)(((float)contractLayoutParams.height * topScale) + topLimit + 0.5f);
        signStaticImageButton.setLayoutParams(layoutParams);
        signStaticImageButton.setVisibility(View.VISIBLE);
        //记录签署坐标
        signImageLeft = layoutParams.leftMargin - (int)(leftLimit + 0.5f);
        signImageTop = layoutParams.topMargin - (int)(topLimit + 0.5f);
    }

    /**
     * 对签署图片的拖动进行边界判断，不能拖动至限制边界之外
     * @param dx 这次拖拽在x轴的移动距离
     * @param dy 这次拖拽在y轴的移动距离
     */
    private void controlDragInLimit(float dx, float dy){
        //获取signImageView此时的左边距和上边距，宽和高
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams)signImageView.getLayoutParams();
        //进行x方向边界判断
        if (dx > 0){//此次将要进行的拖拽在x轴方向向右移动
            //计算移动后的控件位置与右边界的差，getRight()为signImageView的右边界到父View的左边界的距离
            float isOut = rightLimit - (signImageView.getRight() + dx );
            //判断是否超出区域
            if (isOut < 0){//超出区域
                //重新计算在x轴的移动距离
                dx = dx + isOut;
            }
        }else {//此次将要进行的拖拽在x轴方向向左移动
            float isOut = leftLimit - (signImageView.getLeft() + dx );
            if (isOut > 0){//超出区域
                //重新计算在x轴的移动距离
                dx = dx + isOut;
            }
        }
        //进行y方向边界判断
        if (dy > 0){//此次将要进行的拖拽在x轴方向向下移动
            float isOut = bottomLimit - (signImageView.getBottom() + dy );
            if (isOut < 0){//超出区域
                //重新计算在y轴的移动距离
                dy = dy + isOut;
            }
        }else {//此次将要进行的拖拽在x轴方向向上移动
            float isOut = topLimit - (signImageView.getTop() + dy );
            if (isOut > 0){//超出区域
                //重新计算在y轴的移动距离
                dy = dy + isOut;
            }
        }
        //改变signImageView位置,相对父控件的位置
        layoutParams.leftMargin = layoutParams.leftMargin + (int)(dx + 0.5f);
        layoutParams.topMargin = layoutParams.topMargin + (int)(dy + 0.5f);
        signImageView.setLayoutParams(layoutParams);
        //记录此时的坐标
        lastPoint = new PointF(lastPoint.x + dx, lastPoint.y + dy);
        //记录signImageView位置，相对合同图片的位置
        signImageLeft = layoutParams.leftMargin - (int)(leftLimit + 0.5f);
        signImageTop = layoutParams.topMargin - (int)(topLimit + 0.5f);
    }

    /**
     * 设置签署图片的拖拽事件响应
     */
    private void setSignImageViewDrag(){
        signImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 手指压下屏幕
                    case MotionEvent.ACTION_DOWN:
                        //只有一根手指时
                        if (event.getPointerCount() == 1){
                            //记录此时的坐标
                            lastPoint = new PointF(event.getRawX(), event.getRawY());
                        }
                        break;
                    // 手指在屏幕上移动，改事件会被不断触发
                    case MotionEvent.ACTION_MOVE:
                        //一根手指在触摸屏上时
                        if (event.getPointerCount() == 1){
                            float dx = event.getRawX() - lastPoint.x; // 得到x轴的移动距离
                            float dy = event.getRawY() - lastPoint.y; // 得到x轴的移动距离
                            // 进行移动
                            controlDragInLimit(dx, dy);
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 根据signImageButton或signStaticImageButton改变SignImageView位置，
     * 非固定签章情况根据手写图片改变SignImageView宽高比例
     */
    private void adaptSignImageView(){
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams)signImageView.getLayoutParams();
        Button button = null;
        //判断是否固定签章
        if (DataModel.otherData.isStaticPOS()){//是固定签署
            layoutParams.width = ((RelativeLayout.LayoutParams)signStaticImageButton.getLayoutParams()).width;
            layoutParams.height = ((RelativeLayout.LayoutParams)signStaticImageButton.getLayoutParams()).height;
            button = signStaticImageButton;
        }else {//不是固定签署
            //根据手写图片改变SignImageView宽高比例
            int width = DataModel.signatureBitmap.getWidth();
            int height = DataModel.signatureBitmap.getHeight();
            // 计算宽高比例
            float scale = ((float) width) / height;
            layoutParams.height = layoutParams.width / (int)(scale + 0.5f);
            button = signImageButton;
        }
        //将SignImageView位置移动到signImageButton或signStaticImageButton的位置
        layoutParams.leftMargin = button.getRight() - layoutParams.width;
        layoutParams.topMargin = button.getBottom()- layoutParams.height;
        signImageView.setLayoutParams(layoutParams);
        //记录SignImageView的实际宽高
        signImageWidth = layoutParams.width;
        signImageHeight = layoutParams.height;
        //记录签署坐标
        signImageLeft = layoutParams.leftMargin - (int)(leftLimit + 0.5f);
        signImageTop = layoutParams.topMargin - (int)(topLimit + 0.5f);
    }

    /**
     * 做好合同签署的准备，发起签署
     */
    private void readySign(){
        //获取签署所需参数
        String signImageJson = Base64Util.bitmapTobase64(DataModel.signatureBitmap).replace( "+","%2B");
        ViewGroup.LayoutParams layoutParams  = contractImageView.getLayoutParams();
        pageWidth = layoutParams.width;
        pageHeight = layoutParams.height;
        //显示等待动画
        DialogUtil.showWaitingDialog(OtherSignActivity.this,
                Message.getMessage(Message.Message_Contract_Sign_Loading));
        //进行合同签署
        sign(signImageJson);
    }

    /**
     * 发起合同签署
     * @param imageData 手写签署图片数据
     */
    private void sign(String imageData) {
        // 使用Map封装请求参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contractId", DataModel.otherData.getContractId());
        map.put("imageData", imageData);
        map.put("otherData", (DataModel.contractSegue.getOtherData() != null? DataModel.contractSegue.getOtherData(): ""));
        map.put("page", DataModel.otherData.getPage());
        map.put("left", signImageLeft);
        map.put("top", signImageTop);
        map.put("width", signImageWidth);
        map.put("height", signImageHeight);
        map.put("pageWidth", pageWidth);
        map.put("pageHeight", pageHeight);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_SignEventContract);
        // 发送请求
        HttpUtil.postRequestTest(url, json, false, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result)  {
                //判断请求是否成功
                if (status) {//网络请求成功
                    //解析json获取数据
                    Response response = GsonUtil.GsonToBean(result, Response.class);
                    //对签署合同操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功

                        //隐藏等待动画
                        DialogUtil.hiddenWaitingDialog();
                        //下一步
                        SignEndNext();
                    }else {//操作失败
                        DialogUtil.hiddenWaitingDialog();
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(OtherSignActivity.this, message, null);
                    }
                } else {
                    DialogUtil.hiddenWaitingDialog();
                    DialogUtil.showSubmitDialog(OtherSignActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 签署成功后，进行下一步
     */
    private void SignEndNext(){
        //显示提示框
        DialogUtil.showSubmitDialog(OtherSignActivity.this, Message.getMessage(Message.Message_Contract_Other_Sign_Success),
                new DialogUtil.ButtonClickListner() {
                    @Override
                    public void onClick() {
                        backOtherApp(false);
                    }
                });
    }

    /**
     * 返回启动本应用的APP
     */
    private void backOtherApp(boolean isBack){
        //判断手机上是否存在指定包名的应用
        if (PackageUtil.isPkgInstalled(OtherSignActivity.this, DataModel.otherData.getBackUrl())) {//有
            PackageManager packageManager = getPackageManager();
            //跳转到第三方应用签署
            Intent intent = packageManager.getLaunchIntentForPackage(DataModel.otherData.getBackUrl());
            Bundle bundle = new Bundle();
            bundle.putString("code", DataModel.otherData.getCode());
            bundle.putString("action", isBack? "back": "next");
            intent.putExtras(bundle);
            DataModel.otherData.clear();
            DialogUtil.dismissWaitingDialog();
            startActivity(intent);
            finish();
        }else {//没有，提示
            DialogUtil.showSubmitDialog(OtherSignActivity.this, Message.getMessage(Message.Message_Other_App_Not_Found), new DialogUtil.ButtonClickListner() {
                @Override
                public void onClick() {
                    DataModel.otherData.clear();
                    finish();
                }
            });
        }
    }

    /**
     * 去手写签章页面的按钮点击响应
     * @param view 去手写签章页面的按钮
     */
    public void toSignBtnClick(View view){
        Intent intent = new Intent(OtherSignActivity.this, HandSignActivity.class);
        startActivityForResult(intent, REQUESTCODE_HANDSIGN);
    }

    /**
     * 下一步按钮的点击响应
     */
    private void nextBtnClick(){
        if (DataModel.systemSet.getSwitch2() || DataModel.systemSet.getSwitch3() || DataModel.systemSet.getSwitch4()){
            DialogUtil.showSubmitDialog(OtherSignActivity.this, Message.getMessage(Message.Message_Contract_Sign_Capture), new DialogUtil.ButtonClickListner() {
                @Override
                public void onClick() {
                    Intent intent = new Intent(OtherSignActivity.this, InfoCaptureActivity.class);
                    startActivityForResult(intent, REQUESTCODE_INFOCAPTURE);
                }
            });
        }else {
            readySign();
        }
    }
}
