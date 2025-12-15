package rctoys.client.render.entity.model;

import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class PlaneEntityModel extends EntityModel<RCEntityRenderState>
{
	private final ModelPart prop;
	
	public PlaneEntityModel(ModelPart modelPart)
	{
		super(modelPart);
		this.prop = modelPart.getChild("prop");
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(9, 24).addBox(0.0F, -3.0F, 4.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 13).addBox(-1.0F, -2.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
		modelPartData.addOrReplaceChild("wings", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -3.0F, -4.0F, 10.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 6).addBox(1.0F, -3.0F, -4.0F, 10.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 24).addBox(0.0F, -5.0F, 3.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(13, 12).addBox(-2.0F, -2.0F, 4.0F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
		PartDefinition gear = modelPartData.addOrReplaceChild("gear", CubeListBuilder.create().texOffs(24, 19).addBox(0.0F, -1.0F, 6.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -5.0F));
		gear.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 24).addBox(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.75F, 1.0F, 0.0F, 0.0F, 0.7854F));
		gear.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 15).addBox(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -0.75F, 1.0F, 0.0F, 0.0F, -0.7854F));
		modelPartData.addOrReplaceChild("prop", CubeListBuilder.create().texOffs(24, 12).addBox(-2.0F, -0.5F, -0.01F, 4.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -7.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

	public void setupAnim(RCEntityRenderState state)
	{
		super.setupAnim(state);
		
		if(state.enabled)
			this.prop.rotateBy(Axis.ZP.rotationDegrees(state.ageInTicks * 64.0f));
	}
}