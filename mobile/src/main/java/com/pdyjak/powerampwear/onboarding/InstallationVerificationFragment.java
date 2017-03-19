package com.pdyjak.powerampwear.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.Utils;

public class InstallationVerificationFragment extends Fragment {

    private TextView mAppInstalledTextView;
    private View mBigIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.onboarding_installation_verification, container, false
        );
        mAppInstalledTextView = (TextView) view.findViewById(R.id.app_detected_textview);
        mBigIcon = view.findViewById(R.id.big_icon);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isPowerampInstalled(getContext())) {
            mAppInstalledTextView.setText(R.string.installation_detected);
            mBigIcon.setOnClickListener(null);
        } else {
            mAppInstalledTextView.setText(R.string.poweramp_not_installed_onboarding);
            mBigIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGooglePlay();
                }
            });
        }
    }

    private void openGooglePlay() {
        String packageName = getString(R.string.poweramp_package_name);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

}
