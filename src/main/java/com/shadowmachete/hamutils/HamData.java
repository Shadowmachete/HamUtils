package com.shadowmachete.hamutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HamData {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/hamutils-data.json");

    public int numWins = 0;
    public double timeElapsed = 0;

    public void save() {
        try {
            FILE.getParentFile().mkdirs(); // ensure folder exists
            FileWriter writer = new FileWriter(FILE);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HamData load() {
        if (!FILE.exists()) return new HamData();
        try {
            return gson.fromJson(new java.io.FileReader(FILE), HamData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HamData();
        }
    }
}
