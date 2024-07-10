package com.beatcraft.beatmap;

import com.beatcraft.beatmap.data.Info;
import com.beatcraft.render.PhysicalColorNote;
import com.github.fernthedev.beatmap.IColorNote;
import com.github.fernthedev.beatmap.IReadonlyBeatmapData;

import java.util.List;

public class Difficulty {
    public final Info info;
    public final Info.SetDifficulty setDifficulty;
    public final IReadonlyBeatmapData beatmapData;

    public final List<PhysicalColorNote> physicalColorNoteList;

    public Difficulty(Info info, Info.SetDifficulty setDifficulty, IReadonlyBeatmapData beatmapData) {
        this.info = info;
        this.setDifficulty = setDifficulty;
        this.beatmapData = beatmapData;

        physicalColorNoteList = BeatmapJavaInterop
                .getBeatmapItemsList(beatmapData, IColorNote.class)
                .stream()
                .map(x -> new PhysicalColorNote(setDifficulty, x))
                .toList();
    }

}
