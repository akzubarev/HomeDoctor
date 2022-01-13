package com.akzubarev.homedoctor.ui.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.BuildConfig;

import com.akzubarev.homedoctor.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        TextView donate = findViewById(R.id.github);
//        SpannableString ss = new SpannableString("");
//        ss.setSpan(new URLSpan(getString(R.string.link)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        donate.setText(ss);
//        donate.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView versionTV = findViewById(R.id.version);
            String versionTag = "";
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                versionTag = " (beta)";
            String versionText = String.format("Версия: %s%s", versionName, versionTag).trim();
            versionTV.setText(versionText);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goToWeb(View view) {
//        Intent intent = new Intent(this, WebActivity.class);
//        startActivity(intent);
        finish();
    }
}
