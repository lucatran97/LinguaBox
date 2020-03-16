package com.example.linguabox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageSelectAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] countryNames;
    String[] countryCodes;
    LayoutInflater inflter;

    public LanguageSelectAdapter(Context applicationContext, int[] flags, String[] countryNames, String[] countryCodes) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        this.countryCodes = countryCodes;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public String getCountryCode(int i){
        return countryCodes[i];
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public String getItem(int i) {
        return getCountryCode(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(flags[i]);
        names.setText(countryNames[i]);
        return view;
    }
}
