package com.beatcraft.beatmap;

import com.beatcraft.beatmap.data.ColorNote;
import com.beatcraft.beatmap.data.Info;
import com.beatcraft.render.PhysicalColorNote;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class DifficultyV2 extends Difficulty {
    public DifficultyV2(Info info, Info.SetDifficulty setDifficulty) {
        super(info, setDifficulty);
    }

    @Override
    DifficultyV2 load(JsonObject json) {
        loadNotesAndBombs(json);

        return this;
    }

    void loadNotesAndBombs(JsonObject json) {
        JsonArray rawNotes = json.getAsJsonArray("_notes");
        ArrayList<JsonObject> rawBombs = new ArrayList<>();
        ArrayList<JsonObject> rawColorNotes = new ArrayList<>();

        rawNotes.forEach(o -> {
            JsonObject obj = o.getAsJsonObject();
            int type = obj.get("_type").getAsInt();
            if (type == 3) {
                rawBombs.add(obj);
            } else {
                rawColorNotes.add(obj);
            }
        });

        rawColorNotes.forEach(obj -> {
            ColorNote note = new ColorNote().loadV2(obj, this);
            colorNotes.add(new PhysicalColorNote(note));
        });
    }
}
