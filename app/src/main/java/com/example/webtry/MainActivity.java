package com.example.webtry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_PERMISSION_CODE = 1;
    // 获取版本
    final int version = Build.VERSION.SDK_INT;
    // 存放随机数列表
    private ArrayList<Integer> a = new ArrayList<>();
    // 随机数
    private final Random r = new Random();
    // webView控件
    private WebView webView;
    // a二维数组的左右两边1总数的个数
    private int positionstyle;
    // 蓝牙控制器
    public BlueToothController btController = new BlueToothController();
    private final ArrayList<String> requestList = new ArrayList<>();
    // 蓝牙设备
    public BluetoothDevice device;
    // 蓝牙服务器
    public BluetoothSocket bluetoothSocket;
    public Handler mHandler;
    private final BTclient bTclient = new BTclient();
    public readThread readthread;
    private Toast mToast;
    public boolean isStop = false;

    @SuppressLint({"SetJavaScriptEnabled", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        // 获得webView控件
        webView = (WebView) findViewById(R.id.web_1);
        // 获取textView
        // 定时器
        // 文本显示框的获取
        TextView textView = (TextView) findViewById(R.id.textView2);
        // 获取toolbar
        // 自定义toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarx);
        // 关联菜单
        toolbar.inflateMenu(R.menu.toolbar);
        setSupportActionBar(toolbar);
        // 获取webSettings
        WebSettings webSettings = webView.getSettings();
        // 设置JS可行
        webSettings.setJavaScriptEnabled(true);
        // 允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //String htmlString = "<h1>Title</h1><p>This is HTML text<br /><i>Formatted in italics</i><br />Anothor Line</p>";
        //访问网页
        //webView.loadData(htmlString, "text/html", "utf-8");
        // 打开网页
        webView.loadUrl("file:///android_asset/web/cs.html");
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });
        // 绘制三维柱状图
        showPlot();

        //        countDownTimer = new CountDownTimer(10000, 1000) {
//            // 定时，每过1秒执行一次
//            @Override
//            public void onTick(long millisUntilFinished) {
//                // 获取随机数二维数组
//                a = randomA();
////                Log.e(TAG, "onTick: " + a);
//                // 判断数组左右两边1的个数大小关系
//                positionstyle = judgepose(a);
//                // 绘制三维柱状图
//                showPlot();
//                // 如果右边的多
//                if(positionstyle == 1){
//                    textView.setText("center right");
//                // 如果左边的多
//                }else if(positionstyle == -1){
//                    textView.setText("center left");
//                // 如果一样多
//                }else{
//                    textView.setText("right");
//                }
//
//            }
//
//            // 定时结束后回调函数
//            @Override
//            public void onFinish() {
//                showPlot();
//            }
//        };
//        countDownTimer.start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        String s = msg.obj.toString();
                        ArrayList<Integer> digitList = new ArrayList<Integer>();
                        Pattern p = Pattern.compile("[^0-9]");
                        Matcher m = p.matcher(s);
                        String result = m.replaceAll("");
                        for (int i = 0; i < result.length(); i++) {
                            digitList.add(Integer.parseInt(result.substring(i, i+1)));
                        }
//                        Log.e(TAG, "handleMessage: " + digitList);
                        if (digitList.size() == 25){
                            a.clear();
                            a = digitList;
                            positionstyle = judgepose(a);
                            showPlot();
                            // 如果右边的多
                            if(positionstyle == 1){
                                textView.setText("center right");
                            // 如果左边的多
                            }else if(positionstyle == -1){
                                textView.setText("center left");
                            // 如果一样多
                            }else{
                                textView.setText("right");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode) {
                case 1:
                    Bundle bundle = data.getExtras();
                    if (data != null) {
                        String dat = bundle.getString("deviceAddr");
                        Toast.makeText(this, dat, Toast.LENGTH_SHORT).show();
                        device = btController.find_device(dat);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 构建菜单界面布局
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 动态获取权限
     */
    public void getPermision(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestList.add(Manifest.permission.BLUETOOTH_SCAN);
            requestList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            requestList.add(Manifest.permission.BLUETOOTH_CONNECT);
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            requestList.add(Manifest.permission.BLUETOOTH);
        }
        if(requestList.size() != 0){
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[0]), REQ_PERMISSION_CODE);
        }
    }
    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getPermision();
        // 获取点击的控件的id
        int id = item.getItemId();
        // 根据id实现相应功能
        if (id == R.id.toolbarx_r_1){
            if(btController.getBlueToothStatus()) {
                btController.turnOffBlueTooth();
                Toast.makeText(this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
            }
            else{
                btController.turnOnBlueTooth(this, 1);
                Toast.makeText(this, "打开蓝牙中", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.toolbarx_r_2){
            if(btController.getBlueToothStatus()) {
                Intent intent = new Intent(MainActivity.this, BlueToothActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
            else{
                Toast.makeText(this, "蓝牙尚未打开", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 执行JS
     */
    public void showPlot(){
        if (version < 18) {
            for (int i = 0; i < 10; i++) {
                webView.loadUrl("javascript: reformData(" + a + ")");
            };
        } else {
            webView.evaluateJavascript("javascript: reformData(" + a + ")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                }
            });
        }
    }

    /**
     * 随机产生数据
     * @return
     */
    public ArrayList randomA(){
        a.clear();
        for(int i = 0; i < 25; i++){
            a.add(r.nextInt(2));
        }
        return a;
    }

    /**
     * 根据左右两边1的个数判断坐姿
     * 若左边大于右边，则偏左
     * 若右边大于左边，则偏右
     * 左边等于右边，则正确
     * @param a 数据
     * @return
     */
    public int judgepose(ArrayList a){
        Integer[] b = new Integer[a.size()];
        a.toArray(b);

        int right_add_one = 0;
        int left_add_one = 0;
        for (int i = 0; i < 10; i++){
            if (b[i] == 1){
                left_add_one ++;
            }
            if (b[i+15] == 1){
                right_add_one ++;
            }
        }
        return Integer.compare(right_add_one, left_add_one);
    }

    /**
     * 使用JS函数控制html页面，重新开始执行定时函数，刷新10次绘制
     * @param view
     */
    public void JSuseClick(View view) {
//        // 若之前的定时器还没停止，则取消定时
//        countDownTimer.cancel();
//        // 重新设置定时
//        countDownTimer = new CountDownTimer(15000, 1500) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                a = randomA();
////                Log.e(TAG, "onTick: " + a);
//                positionstyle = judgepose(a);
//                showPlot();
//                if(positionstyle == 1){
//                    textView.setText("center right");
//                }else if(positionstyle == -1){
//                    textView.setText("center left");
//                }else{
//                    textView.setText("right");
//                }
//            }
//            @Override
//            public void onFinish() {
//            }
//        };
//        countDownTimer.start();
        // 获取activity_blue_tooth传入的数据
        if (device != null) {
            if (!isStop) {
                readthread = new readThread();
                bTclient.connectDevice(device);
                bTclient.start();
                isStop = !isStop;
            }
        }
        else {
            showToast("未连接蓝牙");
        }
    }

    public void showToast(String text){
        if( mToast == null){
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else{
            mToast.setText(text);
        }
        mToast.show();
    }

    private class BTclient extends Thread{
        private void connectDevice(BluetoothDevice device){
            try {
                getPermision();
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                bluetoothSocket.connect();
                readthread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取数据
    private class readThread extends Thread {

        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = bluetoothSocket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
                        byte[] buf_data = new byte[bytes];
                        System.arraycopy(buffer, 0, buf_data, 0, bytes);
                        String s = new String(buf_data);//接收的值inputstream 为 s
//                        Log.e(TAG, "run: " + s);
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = s;
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}

