package com.example.apppill;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;

public class Medicine {
    public TreeSet<String> Medicines = new TreeSet<>();
    public void init(Context context) throws IOException {
        Context mContext = context;
        InputStream is = null;
        try{
            is = mContext.getAssets().open("Medicines.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] result = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8)).split("\r\n");
        for (String i : result){
            Medicines.add(i.toLowerCase(Locale.ROOT));
        }
    }
    public boolean isMedicine(String m){
        return Medicines.contains(m);
    }
}
