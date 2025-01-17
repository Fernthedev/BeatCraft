package com.beatcraft.animation.pointdefinition;

import com.beatcraft.animation.Easing;

import java.util.ArrayList;

public abstract class PointDefinition<T> {
    protected ArrayList<Point<T>> points = new ArrayList<>();

    abstract protected T interpolatePoints(int a, int b, float time);

    public T interpolate(float time) {
        if (points.isEmpty()) {
            return null;
        }

        Point<T> lastPoint = points.getLast();
        if (lastPoint.time <= time) {
            return lastPoint.value;
        }

        Point<T> firstPoint = points.getFirst();
        if (firstPoint.time >= time) {
            return firstPoint.value;
        }

        TimeIndexInfo indexInfo = searchIndexAtTime(time);
        Point<T> leftPoint = points.get(indexInfo.left);
        Point<T> rightPoint = points.get(indexInfo.right);

        float betweenTime = 0;
        float divisor = rightPoint.time - leftPoint.time;
        if (divisor != 0) {
            betweenTime = (time - leftPoint.time) / divisor;
        }

        betweenTime = rightPoint.easing.apply(betweenTime);

        return interpolatePoints(indexInfo.left, indexInfo.right, betweenTime);
    }

    private record TimeIndexInfo(int left, int right) {}

    private TimeIndexInfo searchIndexAtTime(float time) {
        int left = 0;
        int right = points.size();

        while (left < right - 1) {
            int middle = (left + right) / 2;
            float pointTime = points.get(middle).time;

            if (pointTime < time) {
                left = middle;
            }
            else {
                right = middle;
            }
        }

        return new TimeIndexInfo(left, right);
    }
}
