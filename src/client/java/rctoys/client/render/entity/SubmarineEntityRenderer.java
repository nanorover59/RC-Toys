package rctoys.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;
import rctoys.client.RCToysModClient;
import rctoys.client.render.entity.model.PlaneEntityModel;
import rctoys.client.render.entity.model.SubmarineEntityModel;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class SubmarineEntityRenderer extends AbstractRCEntityRenderer
{
	private static final Identifier BASE = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/submarine/submarine_base.png");
	private static final Identifier COLOR = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/submarine/submarine_color.png");
	private static final Identifier EMISSIVE = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/submarine/submarine_emissive.png");

	private final EntityModel<RCEntityRenderState> model;

	public SubmarineEntityRenderer(Context context)
	{
		super(context);
		this.model = new SubmarineEntityModel(context.bakeLayer(RCToysModClient.MODEL_SUBMARINE_LAYER));
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