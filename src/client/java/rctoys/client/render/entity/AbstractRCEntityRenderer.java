package rctoys.client.render.entity;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import rctoys.client.render.entity.state.RCEntityRenderState;
import rctoys.entity.AbstractRCEntity;

public abstract class AbstractRCEntityRenderer extends EntityRenderer<AbstractRCEntity, RCEntityRenderState>
{
	public AbstractRCEntityRenderer(Context context)
	{
		super(context);
	}

    @Override
	public void render(RCEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState)
	{
		matrices.push();
		matrices.translate(0.0f, state.height * 0.5f, 0.0f);
		matrices.multiply(state.quaternion);
        matrices.scale(-1.0f, -1.0f, 1.0f);

        queue.submitModel(getModel(), state, matrices, RenderLayer.getEntityCutoutNoCull(getBaseTexture()), state.light, OverlayTexture.DEFAULT_UV, Colors.WHITE, null, state.outlineColor, null);
        queue.submitModel(getModel(), state, matrices, RenderLayer.getEntityCutoutNoCull(getColorTexture()), state.light, OverlayTexture.DEFAULT_UV, (state.color & 0x00FFFFFF) | 0xFF000000, null, state.outlineColor, null);

        if(state.enabled)
            queue.submitModel(getModel(), state, matrices, RenderLayer.getEyes(getEmissiveTexture()), state.light, OverlayTexture.DEFAULT_UV, Colors.WHITE, null, state.outlineColor, null);

		matrices.pop();
		super.render(state, matrices, queue, cameraState);
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