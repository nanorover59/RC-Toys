package rctoys.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import rctoys.client.render.entity.state.RCEntityRenderState;
import rctoys.entity.AbstractRCEntity;

public abstract class AbstractRCEntityRenderer extends EntityRenderer<AbstractRCEntity, RCEntityRenderState>
{
	public AbstractRCEntityRenderer(Context context)
	{
		super(context);
	}

    @Override
	public void submit(RCEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrices.pushPose();
		matrices.translate(0.0f, state.boundingBoxHeight * 0.5f, 0.0f);
        matrices.mulPose(state.quaternion);
        matrices.scale(-1.0f, -1.0f, 1.0f);

        queue.submitModel(getModel(), state, matrices, RenderTypes.entityCutoutNoCull(getBaseTexture()), state.lightCoords, OverlayTexture.NO_OVERLAY, CommonColors.WHITE, null, state.outlineColor, null);
        queue.submitModel(getModel(), state, matrices, RenderTypes.entityCutoutNoCull(getColorTexture()), state.lightCoords, OverlayTexture.NO_OVERLAY, (state.color & 0x00FFFFFF) | 0xFF000000, null, state.outlineColor, null);

        if(state.enabled)
            queue.submitModel(getModel(), state, matrices, RenderTypes.eyes(getEmissiveTexture()), state.lightCoords, OverlayTexture.NO_OVERLAY, CommonColors.WHITE, null, state.outlineColor, null);

		matrices.popPose();
		super.submit(state, matrices, queue, cameraState);
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

    @Override
	public void extractRenderState(AbstractRCEntity entity, RCEntityRenderState state, float tickProgress)
	{
		super.extractRenderState(entity, state, tickProgress);
		state.color = entity.getColor();
		state.enabled = entity.isEnabled();
		state.quaternion = entity.getLerpedQuaternion(tickProgress);
	}
}