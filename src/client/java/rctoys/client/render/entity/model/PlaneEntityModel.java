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
import net.minecraft.util.math.RotationAxis;
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

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 12).cuboid(-1.0F, -3.0F, -6.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)).uv(9, 24).cuboid(0.0F, -3.0F, 4.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(24, 13).cuboid(-1.0F, -2.0F, -7.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 2.0F, 0.0F));
		modelPartData.addChild("wings", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -3.0F, -4.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.0F)).uv(0, 6).cuboid(1.0F, -3.0F, -4.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.0F)).uv(0, 24).cuboid(0.0F, -5.0F, 3.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)).uv(13, 12).cuboid(-2.0F, -2.0F, 4.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 2.0F, 0.0F));
		ModelPartData gear = modelPartData.addChild("gear", ModelPartBuilder.create().uv(24, 19).cuboid(0.0F, -1.0F, 6.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 2.0F, -5.0F));
		gear.addChild("cube_r1", ModelPartBuilder.create().uv(16, 24).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.5F, -0.75F, 1.0F, 0.0F, 0.0F, 0.7854F));
		gear.addChild("cube_r2", ModelPartBuilder.create().uv(24, 15).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -0.75F, 1.0F, 0.0F, 0.0F, -0.7854F));
		modelPartData.addChild("prop", ModelPartBuilder.create().uv(24, 12).cuboid(-2.0F, -0.5F, -0.01F, 4.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -7.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(RCEntityRenderState state)
	{
		super.setAngles(state);
		
		if(state.enabled)
			this.prop.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(state.age * 64.0f));
	}
}