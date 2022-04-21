package com.akzubarev.homedoctor.ui.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.BuildConfig;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.databinding.FragmentInfoBinding;
import com.akzubarev.homedoctor.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoFragment extends Fragment {

    private DatabaseReference mDatabase;
    private static final String TAG = "InfoFragment";
    private FragmentInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        TextView donate = findViewById(R.id.github);
//        SpannableString ss = new SpannableString("");
//        ss.setSpan(new URLSpan(getString(R.string.link)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        donate.setText(ss);
//        donate.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            Activity activity = getActivity();
            PackageManager pcm = activity.getPackageManager();
            String versionName = pcm.getPackageInfo(activity.getPackageName(), 0).versionName;
            TextView versionTV = binding.version;
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
//        finish();
    }
}