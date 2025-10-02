package rctoys.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;
import rctoys.RCToysMod;
import rctoys.client.RCToysModClient;
import rctoys.client.render.entity.model.PlaneEntityModel;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class PlaneEntityRenderer extends AbstractRCEntityRenderer
{
	private static final Identifier BASE = Identifier.of(RCToysMod.MOD_ID, "textures/entity/plane/plane_base.png");
	private static final Identifier COLOR = Identifier.of(RCToysMod.MOD_ID, "textures/entity/plane/plane_color.png");
	private static final Identifier EMISSIVE = Identifier.of(RCToysMod.MOD_ID, "textures/entity/plane/plane_emissive.png");
	
	private final EntityModel<RCEntityRenderState> model;
	
	public PlaneEntityRenderer(Context context)
	{
		super(context);
		this.model = new PlaneEntityModel(context.getPart(RCToysModClient.MODEL_PLANE_LAYER));
	}

	@Override
	protected EntityModel<RCEntityRenderState> getModel()
	{
		return model;
	}

	@Override
	protected Identifier getBaseTexture()
	{
		return BASE;
	}

	@Override
	protected Identifier getColorTexture()
	{
		return COLOR;
	}

	@Override
	protected Identifier getEmissiveTexture()
	{
		return EMISSIVE;
	}
}