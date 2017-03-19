package com.pdyjak.powerampwear.music_browser.files;

import android.support.annotation.NonNull;
import android.view.View;

import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase;

import java.util.Locale;

class FileViewHolder extends SimpleTwoLineItemViewHolderBase {
    FileViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindWith(@NonNull Object object) {
        super.bindWith(object);
        FileItem item = (FileItem) object;
        StringBuilder sb = new StringBuilder();
        long hours = item.duration / 3600;
        long minutes = (item.duration % 3600) / 60;
        long seconds = item.duration % 60;
        String formattingStr = "%02d";
        if (hours > 0) sb.append(String.format(Locale.US, formattingStr, hours)).append(":");
        sb.append(String.format(Locale.US, formattingStr, minutes)).append(":");
        sb.append(String.format(Locale.US, formattingStr, seconds));

        mFirstLine.setText(String.format(Locale.US, "%s : %s", item.title, sb.toString()));
        mSecondLine.setText(String.format(Locale.US, "%s - %s", item.artist, item.album));
    }
}
