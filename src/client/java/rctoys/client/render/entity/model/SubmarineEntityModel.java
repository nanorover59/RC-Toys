package rctoys.client.render.entity.model;

import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class SubmarineEntityModel extends EntityModel<RCEntityRenderState> {
    private final ModelPart prop;

    public SubmarineEntityModel(ModelPart root) {
        super(root);
        this.prop = root.getChild("prop");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.0F, -5.0F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(8, 13).addBox(-1.0F, -3.5F, 5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 13).addBox(-1.0F, -3.5F, -6.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-0.5F, -5.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
        PartDefinition fins1 = modelPartData.addOrReplaceChild("fins1", CubeListBuilder.create(), PartPose.offset(-0.75F, -0.5F, 1.0F));

        fins1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        fins1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        modelPartData.addOrReplaceChild("fins2", CubeListBuilder.create().texOffs(8, 16).addBox(-1.75F, -2.5F, -5.5F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 16).addBox(0.75F, -2.5F, -5.5F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 18).addBox(0.0F, -4.5F, -5.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 18).addBox(0.0F, -1.5F, -5.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition prop = modelPartData.addOrReplaceChild("prop", CubeListBuilder.create().texOffs(14, 18).addBox(-1.0F, 0.0F, -7.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(0.0F, -1.0F, -7.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        return LayerDefinition.create(modelData, 32, 32);
    }

    public void setupAnim(RCEntityRenderState state)
    {
        super.setupAnim(state);

        if(state.enabled)
            this.prop.rotateBy(Axis.ZP.rotationDegrees(state.ageInTicks * 64.0f));
    }
}
