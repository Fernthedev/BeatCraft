package com.beatcraft.beatmap

import com.github.fernthedev.beatmap.IBeatmapDataItem
import com.github.fernthedev.beatmap.IReadonlyBeatmapData

object BeatmapJavaInterop {
    @JvmStatic
    fun <T : IBeatmapDataItem> getBeatmapItems(beatmapData: IReadonlyBeatmapData, clazz: Class<T>): Iterable<T> {
        return beatmapData.getBeatmapItems(clazz).asIterable()
    }

    @JvmStatic
    fun <T : IBeatmapDataItem> getBeatmapItemsList(beatmapData: IReadonlyBeatmapData, clazz: Class<T>): List<T> {
        return getBeatmapItems(beatmapData, clazz).toList()
    }

}