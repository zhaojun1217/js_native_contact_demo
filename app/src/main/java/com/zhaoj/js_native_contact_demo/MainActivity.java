package com.zhaoj.js_native_contact_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private BridgeWebView webView;
    private Button btnDefaultTest, btnCustomTest;
    private TextView tvTextInfo, tvReloadUrl;
    private String TAG = "WebView";

    static class Location {
        String address;

        public Location(String address) {
            this.address = address;
        }
    }

    static class User {
        String name;
        Location location;
        String testStr;

        public User(String name, Location location, String testStr) {
            this.name = name;
            this.location = location;
            this.testStr = testStr;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextInfo = findViewById(R.id.tvTextInfo);
        tvReloadUrl = findViewById(R.id.tvReloadUrl);
        webView = findViewById(R.id.testWebView);
        btnCustomTest = findViewById(R.id.btnCustomTest);
        btnDefaultTest = findViewById(R.id.btnDefaultTest);

        // 设置浏览器链接
        webView.setWebChromeClient(new WebChromeClient());
        // 加载url
        webView.loadUrl("file:///android_asset/demo.html");

        // 注册js监听回调，js通过默认方法发送消息给native时，可以在此处接收到
        webView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                new AlertDialog.Builder(MainActivity.this).setMessage(data.toString()).show();
                tvTextInfo.setText(data.toString());
                function.onCallBack("Native已经收到了默认方法的消息，Js请知悉！");
            }
        });

        // 注册js监听回调，js通过 submitFromWeb 方法发送消息给native时，可以在此处接收到
        webView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                new AlertDialog.Builder(MainActivity.this).setMessage(data.toString()).show();
                tvTextInfo.setText(data.toString());
                function.onCallBack("Native已经收到了名为submitFromWeb方法的消息，Js请知悉！");
            }
        });

        // native主动发消息给js（默认handleName方法）
        btnDefaultTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.send("这里是native向js发送的默认消息！", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        tvTextInfo.setText(data.toString());
                        new AlertDialog.Builder(MainActivity.this).setMessage("通过默认方法发消息给js，并收到了js的反馈如下\n" + data).show();
                    }
                });
            }
        });

        // native主动发消息给js（handleName = "functionInJs" 的方法）
        btnCustomTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.callHandler("functionInJs", new Gson().toJson(new User("小白兔", new Location("大兴安岭"), "这里是native向js发送的名字叫做'functionInJs'消息!")), new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        tvTextInfo.setText(data.toString());
                        new AlertDialog.Builder(MainActivity.this).setMessage("通过functionInJs方法发消息给js，并收到了js的反馈如下\n" + data).show();
                    }
                });
            }
        });

        // 重新加载网页
        tvReloadUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("file:///android_asset/demo.html");
            }
        });
    }
}
