package rctoys.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class CarEntityModel extends EntityModel<RCEntityRenderState>
{
	public CarEntityModel(ModelPart modelPart)
	{
		super(modelPart);
	}
	
	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -4.0F, 4.0F, 2.0F, 8.0F, new Dilation(0.0F))
		.uv(0, 10).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 1.5F, 0.0F));
		modelPartData.addChild("wheels", ModelPartBuilder.create().uv(18, 10).cuboid(-1.0F, -2.0F, -0.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 10).cuboid(-1.0F, -2.0F, 4.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 10).cuboid(-5.0F, -2.0F, -0.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 10).cuboid(-5.0F, -2.0F, 4.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(2.5F, 2.0F, -3.0F));
		modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -4.5F, 2.0F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 14).cuboid(-2.0F, -4.5F, 2.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(18, 16).cuboid(2.0F, -4.5F, 2.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 2.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
}