package com.akzubarev.homedoctor.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akzubarev.homedoctor.R;

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connected = CheckForConnection();
        if (connected) {
            setContentView(R.layout.activity_web);
            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new MyWebViewClient());
            webView.loadUrl("http://math-trainer.ru");
        } else {
            setContentView(R.layout.activity_web_no_internet);
//            TextView messagetext = findViewById(R.id.message);
//            messagetext.setText(message);
        }


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private boolean CheckForConnection() {

        boolean internetConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            message = "Build version > M";
//            Network nw = connectivityManager.getActiveNetwork();
//            if (nw == null) {
//                internetConnection = false;
//                message += ", nw==null";
//            } else {
//                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
//                internetConnection = actNw != null && (
//                        actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)// ||
//                    //    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
//                );
//
//                message += actNw == null ? ", actNw==null" : ", no transport";
//            }
        //      } else {
        String message = "Build version < M";
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        internetConnection = nwInfo != null && nwInfo.isConnected();
        message += nwInfo == null ? ", nwInfo==null" : ", nw is not connected";
//        }
        return internetConnection;
    }

    @Override
    public void onBackPressed() {
        if (connected && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private static class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        // Для старых устройств
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}


