package com.beatcraft.beatmap.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Color {
    public float red = 0;
    public float green = 0;
    public float blue = 0;
    public float alpha = 0;

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color() {}

    public static Color fromJsonObject(JsonObject json) {
        Color color = new Color();
        color.red = json.get("r").getAsFloat();
        color.green = json.get("g").getAsFloat();
        color.blue = json.get("b").getAsFloat();
        return color;
    }

    public static Color fromJsonArray(JsonArray json) {
        Color color = new Color();
        color.red = json.get(0).getAsFloat();
        color.green = json.get(1).getAsFloat();
        color.blue = json.get(2).getAsFloat();
        return color;
    }

    public static Color fromArray(float[] json) {
        Color color = new Color();
        color.red = json[0];
        color.green = json[1];
        color.blue = json[2];
        color.alpha = json[3];
        return color;
    }
    public static Color fromArray(Float[] json) {
        Color color = new Color();
        color.red = json[0];
        color.green = json[1];
        color.blue = json[2];
        color.alpha = json[3];
        return color;
    }
}