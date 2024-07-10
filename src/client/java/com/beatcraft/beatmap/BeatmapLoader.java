package com.beatcraft.beatmap;

import com.beatcraft.beatmap.data.Info;
import com.github.fernthedev.beatmap.IBeatmapLoader;
import com.github.fernthedev.beatmap.IColorNote;
import com.github.fernthedev.beatmap.IReadonlyBeatmapData;
import com.github.fernthedev.beatmap.impl.v3.BeatmapDataV3;
import com.github.fernthedev.beatmap.impl.v3.BombNoteDataV3;
import com.github.fernthedev.beatmap.impl.v3.ColorNoteDataV3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlinx.serialization.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BeatmapLoader {
    private static IBeatmapLoader beatmapLoader = new com.github.fernthedev.beatmap.impl.BeatmapLoader();

    public static Info getInfoFromFile(String path) throws IOException {
        String jsonString = Files.readString(Paths.get(path));
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

        Info info = Info.from(json, path);

        JsonArray styleSetsRaw = json.get("_difficultyBeatmapSets").getAsJsonArray();
        styleSetsRaw.forEach(styleSetRaw -> {
            JsonObject styleSetObject = styleSetRaw.getAsJsonObject();
            Info.StyleSet styleSet = new Info.StyleSet();

            String styleKey = styleSetObject.get("_beatmapCharacteristicName").getAsString();
            info.styleSets.put(styleKey, styleSet);

            JsonArray difficultiesRaw = styleSetObject.get("_difficultyBeatmaps").getAsJsonArray();
            difficultiesRaw.forEach(difficultyRaw -> {
                JsonObject difficultyObject = difficultyRaw.getAsJsonObject();
                Info.SetDifficulty setDifficulty = Info.SetDifficulty.from(difficultyObject, info);
                String fileName = difficultyObject.get("_beatmapFilename").getAsString();
                styleSet.difficulties.put(fileName, setDifficulty);
            });
        });

        return info;
    }

    public static String getPathFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public static Info.SetDifficulty getSetDifficulty(String fileName, Info info) {
        for (Info.StyleSet styleSet : info.styleSets.values()) {
            for (var entry : styleSet.difficulties.entrySet()) {
                if (Objects.equals(entry.getKey(), fileName)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private static int getMajorVersion(JsonObject json) {
        String version;
        if (json.has("version")) {
            version = json.get("version").getAsString();
        } else {
            version = json.get("_version").getAsString();
        }
        return Integer.parseInt(version.substring(0, 1));
    }

    public static Difficulty getDifficultyFromFile(String path, Info info) throws IOException {
        String fileName = getPathFileName(path);
        Info.SetDifficulty setDifficulty = getSetDifficulty(fileName, info);
        return getDifficultyFromFile(path, setDifficulty, info);
    }
    public static Difficulty getDifficultyFromFile(String path, Info.SetDifficulty setDifficulty, Info info) throws IOException {
        String jsonString = Files.readString(Paths.get(path));

        IReadonlyBeatmapData beatmapData = beatmapLoader.loadBeatmap(jsonString);

        beatmapData = process(beatmapData);

        return new Difficulty(info, setDifficulty, beatmapData);
    }

    private static IReadonlyBeatmapData process(IReadonlyBeatmapData beatmapData) {
        // get fake notes
        if (beatmapData instanceof BeatmapDataV3 beatmapDataV3) {
            Map<String, JsonElement> customData = beatmapDataV3.getCustomData();

            var fakeColorNotes = customData.getOrDefault("fakeColorNotes", null);
            if (fakeColorNotes != null) {
                List<ColorNoteDataV3> notes = JsonUtil.parseNotesV3(fakeColorNotes);

                for (ColorNoteDataV3 note : notes) {
                    beatmapDataV3.add(note);
                }
            }

            var fakeBombs = customData.getOrDefault("fakeBombs", null);
            if (fakeBombs != null) {
                List<BombNoteDataV3> notes = JsonUtil.parseBombsV3(fakeBombs);

                for (BombNoteDataV3 note : notes) {
                    beatmapDataV3.add(note);
                }
            }
        }

        return beatmapData;
    }
}
