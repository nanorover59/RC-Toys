package rctoys.client.render.entity;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import rctoys.client.render.entity.state.RCEntityRenderState;
import rctoys.entity.AbstractRCEntity;

public abstract class AbstractRCEntityRenderer extends EntityRenderer<AbstractRCEntity, RCEntityRenderState>
{
	public AbstractRCEntityRenderer(Context context)
	{
		super(context);
	}
	
	public void render(RCEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		matrices.translate(0.0f, state.height * 0.5f, 0.0f);
		matrices.multiply(state.quaternion);
        matrices.scale(-1.0f, -1.0f, 1.0f);
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getBaseTexture()));
		this.getModel().setAngles(state);
		this.getModel().render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
		consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getColorTexture()));
		this.getModel().render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, (state.color & 0x00FFFFFF) | 0xFF000000);
		
		if(state.enabled)
		{
			consumer = vertexConsumers.getBuffer(RenderLayer.getEyes(getEmissiveTexture()));
			this.getModel().render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
		}
		
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}
	
	protected abstract EntityModel<RCEntityRenderState> getModel();
	
	protected abstract Identifier getBaseTexture();
	
	protected abstract Identifier getColorTexture();
	
	protected abstract Identifier getEmissiveTexture();

	@Override
	public RCEntityRenderState createRenderState()
	{
		return new RCEntityRenderState();
	}
	
	public void updateRenderState(AbstractRCEntity entity, RCEntityRenderState state, float tickProgress)
	{
		super.updateRenderState(entity, state, tickProgress);
		state.color = entity.getColor();
		state.enabled = entity.isEnabled();
		state.quaternion = entity.getLerpedQuaternion(tickProgress);
	}
}