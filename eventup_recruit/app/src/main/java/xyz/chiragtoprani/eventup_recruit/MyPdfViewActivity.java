package xyz.chiragtoprani.eventup_recruit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;




/**
 * Created by Chirag on 10/1/2016.
 */

public class MyPdfViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getExtras().getString("resume");


        System.out.println(url);
        WebView mWebView=new WebView(MyPdfViewActivity.this);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setPluginsEnabled(true);


        mWebView.loadUrl(url);
        setContentView(mWebView);
    }
}
