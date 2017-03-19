package com.pdyjak.powerampwear.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.MusicLibraryCache;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        CheckBox circularScrolling = (CheckBox) view.findViewById(R.id.circular_scrolling_checkbox);
        circularScrolling.setChecked(getSettingsManager().useCircularScrollingGesture());
        circularScrolling.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSettingsManager().saveUseCircularScrolling(isChecked);
            }
        });
        view.findViewById(R.id.invalidate_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicLibraryCache().invalidate();
                Toast.makeText(SettingsFragment.this.getActivity(), R.string.cache_invalidated,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @NonNull
    private SettingsManager getSettingsManager() {
        return ((App) getActivity().getApplicationContext()).getSettingsManager();
    }

    @NonNull
    private MusicLibraryCache getMusicLibraryCache() {
        return ((App) getActivity().getApplicationContext()).getCache();
    }
}
