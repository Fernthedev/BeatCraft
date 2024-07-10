package com.beatcraft.render;

import com.beatcraft.animation.Easing;
import com.beatcraft.beatmap.JsonUtil;
import com.beatcraft.beatmap.data.Info;
import com.beatcraft.math.GenericMath;
import com.beatcraft.math.NoteMath;
import com.github.fernthedev.beatmap.IBeatmapObject;
import kotlinx.serialization.json.JsonElement;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

public abstract class PhysicalBeatmapObject<T extends IBeatmapObject> extends WorldRenderer {
    private static final float JUMP_FAR_Z = 500;
    private static final float JUMP_SECONDS = 0.4f;
    protected static final float SIZE_SCALAR = 0.5f;
    private final Quaternionf spawnQuaternion = SpawnQuaternionPool.getRandomQuaternion();
    protected Quaternionf baseRotation = new Quaternionf();
    public T data;
    NoteMath.Jumps jumps;
    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);

    public final float njs;
    public final float offset;

    PhysicalBeatmapObject(Info.SetDifficulty setDifficulty, T data) {
        Map<String, JsonElement> customData = data.getCustomData();

        if (customData.containsKey("_noteJumpMovementSpeed"))
            njs = JsonUtil.jsonElementToFloat(customData.get("_noteJumpMovementSpeed"));
        else {
            njs = setDifficulty.njs;
        }

        if (customData.containsKey("_noteJumpStartBeatOffset")) {
            offset = JsonUtil.jsonElementToFloat(customData.get("_noteJumpStartBeatOffset"));
        }
        else {
            offset = setDifficulty.offset;
        }

        this.data = data;
        this.jumps = NoteMath.getJumps(njs, offset, BeatmapPlayer.currentInfo.bpm);
    }

    public float getSpawnBeat() {
        return data.getTime() - jumps.halfDuration();
    }

    public float getDespawnBeat() {
        return data.getTime() + jumps.halfDuration();
    }

    public boolean shouldRender() {
        float margin = GenericMath.secondsToBeats(JUMP_SECONDS, BeatmapPlayer.currentInfo.bpm);
        boolean isAboveSpawnBeat = BeatmapPlayer.getCurrentBeat() >= getSpawnBeat() - margin;
        boolean isBelowDespawnBeat = BeatmapPlayer.getCurrentBeat() <= getDespawnBeat() + margin;
        return isAboveSpawnBeat && isBelowDespawnBeat;
    }

    public void updateTime(float time) {
        doJumpsPosition(time);
        doSpawnAnimation(time);
    }

    protected void doJumpsPosition(float time) {
        float spawnPosition = jumps.jumpDistance() / 2;
        float despawnPosition = -spawnPosition;

        float spawnBeat = getSpawnBeat();
        float despawnBeat = getDespawnBeat();

        // jumps
        if (time < spawnBeat) {
            // jump in
            float percent = (spawnBeat - time) / 2;
            position.z = Math.lerp(spawnPosition, JUMP_FAR_Z, percent);
        } else if (time > despawnBeat) {
            // jump out
            float percent = (time - despawnBeat) / 2;
            position.z = Math.lerp(despawnPosition, -JUMP_FAR_Z, percent);
        } else {
            // in between
            float percent = (time - spawnBeat) / (despawnBeat - spawnBeat);
            position.z = Math.lerp(spawnPosition, despawnPosition, percent);
        }
    }

    protected Vector2f get2DPosition() {
        return new Vector2f(
                (this.data.getLineIndex() - 1.5f) * 0.6f * -1,
                (this.data.getNoteLineLayer()) * 0.6f
        );
    }

    protected void doSpawnAnimation(float time) {
        float lifetime = GenericMath.clamp01(GenericMath.inverseLerp(getDespawnBeat(), getSpawnBeat(), time));
        float spawnLifetime = GenericMath.clamp01(1 - ((lifetime - 0.5f) * 2));
        float jumpTime = Easing.easeOutQuad(spawnLifetime);

        Vector2f grid = get2DPosition();
        position.x = grid.x;
        position.y = Math.lerp(1.1f, grid.y + 1.1f, jumpTime);

        if (lifetime > 0.5) {
            doSpawnRotation(spawnLifetime);
        }
    }

    protected void doSpawnRotation(float spawnLifetime) {
        float rotationLifetime = GenericMath.clamp01(spawnLifetime / 0.3f);
        float rotationTime = Easing.easeOutQuad(rotationLifetime);
        rotation = new Quaternionf().set(spawnQuaternion).slerp(baseRotation, rotationTime);
    }

    @Override
    protected void worldRender(MatrixStack matrices, VertexConsumer vertexConsumer) {
        if (!shouldRender()) return;

        updateTime(BeatmapPlayer.getCurrentBeat());
        matrices.translate(position.x, position.y, position.z);
        matrices.scale(scale.x * SIZE_SCALAR, scale.y * SIZE_SCALAR, scale.z * SIZE_SCALAR);
        matrices.multiply(rotation);
        matrices.translate(-0.5, -0.5, -0.5);

        objectRender(matrices, vertexConsumer);
    }

    abstract protected void objectRender(MatrixStack matrices, VertexConsumer vertexConsumer);
}
