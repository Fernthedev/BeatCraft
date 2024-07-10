package com.beatcraft.render;

import com.beatcraft.beatmap.JsonUtil;
import com.beatcraft.beatmap.data.Color;
import com.beatcraft.beatmap.data.Info;
import com.beatcraft.math.NoteMath;
import com.github.fernthedev.beatmap.IColorNote;
import com.github.fernthedev.beatmap.NoteCutDirection;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.List;

public class PhysicalColorNote extends PhysicalBeatmapObject<IColorNote> {
    public static final ModelIdentifier colorNoteArrowModelID = new ModelIdentifier("beatcraft", "color_note_arrow", "inventory");
    public static final ModelIdentifier colorNoteDotModelID = new ModelIdentifier("beatcraft", "color_note_dot", "inventory");
    private static final int overlay = OverlayTexture.getUv(0, false);

    public final Color color;

    public PhysicalColorNote(Info.SetDifficulty setDifficulty, IColorNote data) {
        super(setDifficulty, data);

        var colorJson = data.getCustomData().getOrDefault("color", null);
        if (colorJson == null) {
            colorJson = data.getCustomData().getOrDefault("_color", null);
        }

        if (colorJson != null) {
            List<Float> floats = JsonUtil.parseArray(colorJson, Float.class);
            this.color = Color.fromArray(floats.toArray(new Float[0]));
        }
        else {
            this.color = null;
        }

        Quaternionf baseRotation = NoteMath.rotationFromCut(data.getCutDirection());
        baseRotation.rotateZ(Math.toRadians(data.getCutDirectionAngleOffset()));
        this.baseRotation = baseRotation;
    }


    @Override
    protected void objectRender(MatrixStack matrices, VertexConsumer vertexConsumer) {
        var localPos = matrices.peek();

        BakedModel model;
        if (data.getCutDirection() == NoteCutDirection.Any) {
            model = mc.getBakedModelManager().getModel(colorNoteDotModelID);
        } else {
            model = mc.getBakedModelManager().getModel(colorNoteArrowModelID);
        }
        mc.getBlockRenderManager().getModelRenderer().render(localPos, vertexConsumer, null, model, color.red, color.green, color.blue, 255, overlay);
    }
}
