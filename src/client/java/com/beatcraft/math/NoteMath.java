package com.beatcraft.math;

import com.github.fernthedev.beatmap.NoteCutDirection;
import org.joml.Math;
import org.joml.Quaternionf;

public class NoteMath {
    public static Jumps getJumps(float njs, float offset, float bpm) {
        float hjd = 4;
        float num = 60 / bpm;

        while (njs * num * hjd > 17.999f)
            hjd /= 2;

        hjd += offset;

        if (hjd < 0.25f) hjd = 0.25f;
        float jd = hjd * num * njs * 2;

        return new Jumps(hjd, jd);
    }

    public record Jumps(float halfDuration, float jumpDistance) {}

    private static Quaternionf rotationZDegrees(float degrees) {
        return new Quaternionf().rotateZ(Math.toRadians(degrees));
    }

    public static Quaternionf rotationFromCut(NoteCutDirection cutDirection) {
        return switch (cutDirection) {
            case NoteCutDirection.Up -> rotationZDegrees(180);
            case NoteCutDirection.Down, NoteCutDirection.Any, NoteCutDirection.None -> new Quaternionf();
            case NoteCutDirection.Left -> rotationZDegrees(90);
            case NoteCutDirection.Right -> rotationZDegrees(-90);
            case NoteCutDirection.UpLeft-> rotationZDegrees(135);
            case NoteCutDirection.UpRight-> rotationZDegrees(-135);
            case NoteCutDirection.DownLeft-> rotationZDegrees(45);
            case NoteCutDirection.DownRight -> rotationZDegrees(-45);
        };
    }
}
