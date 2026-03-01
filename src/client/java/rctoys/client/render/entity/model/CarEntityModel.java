package rctoys.client.render.entity.model;

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
public class CarEntityModel extends EntityModel<RCEntityRenderState>
{
	public CarEntityModel(ModelPart modelPart)
	{
		super(modelPart);
	}
	
	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 10).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
		modelPartData.addOrReplaceChild("wheels", CubeListBuilder.create().texOffs(18, 10).addBox(-1.0F, -2.0F, -0.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 10).addBox(-1.0F, -2.0F, 4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 10).addBox(-5.0F, -2.0F, -0.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 10).addBox(-5.0F, -2.0F, 4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 2.0F, -3.0F));
		modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.5F, 2.0F, 4.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 14).addBox(-2.0F, -4.5F, 2.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 16).addBox(2.0F, -4.5F, 2.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}
}