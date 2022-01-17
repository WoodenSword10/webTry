
package com.example.webtry;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // 获取版本
    final int version = Build.VERSION.SDK_INT;
    private ArrayList<Integer> a = new ArrayList<>();
    private Random r = new Random();
    // 消息字符
    private String TAG = "";
    // webView控件
    private WebView webView;
    // 定时器
    private CountDownTimer countDownTimer;
    private TextView textView;
    private int positionstyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获得webView控件
        webView = (WebView) findViewById(R.id.web_1);
        // 获取textView
        textView = (TextView)findViewById(R.id.textView2);
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
        showPlot();
        /**
         * CountDownTimer 实现倒计时
         */
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                a = randomA();
//                Log.e(TAG, "onTick: " + a);
                positionstyle = judgepose(a);
                showPlot();
                if(positionstyle == 1){
                    textView.setText("center right");
                }else if(positionstyle == -1){
                    textView.setText("center left");
                }else{
                    textView.setText("right");
                }

            }

            @Override
            public void onFinish() {
                showPlot();
            }
        };
//        countDownTimer.start();
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

    public ArrayList randomA(){
        a.clear();
        for(int i = 0; i < 25; i++){
            a.add(r.nextInt(2));
        }
        return a;
    }

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
        if(right_add_one > left_add_one){
            return 1;
        }
        else if(right_add_one < left_add_one){
            return -1;
        }
        else{
            return 0;
        }
    }

    public void JSuseClick(View view) {
        countDownTimer.cancel();
        countDownTimer = new CountDownTimer(15000, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {
                a = randomA();
//                Log.e(TAG, "onTick: " + a);
                positionstyle = judgepose(a);
                showPlot();
                if(positionstyle == 1){
                    textView.setText("center right");
                }else if(positionstyle == -1){
                    textView.setText("center left");
                }else{
                    textView.setText("right");
                }
            }

            @Override
            public void onFinish() {
            }
        };
        countDownTimer.start();
    }
}

