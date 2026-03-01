package rctoys.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;
import rctoys.client.RCToysModClient;
import rctoys.client.render.entity.model.CarEntityModel;
import rctoys.client.render.entity.state.RCEntityRenderState;

@Environment(EnvType.CLIENT)
public class CarEntityRenderer extends AbstractRCEntityRenderer
{
	private static final Identifier BASE = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/car/car_base.png");
	private static final Identifier COLOR = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/car/car_color.png");
	private static final Identifier EMISSIVE = Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "textures/entity/car/car_emissive.png");
	
	private final EntityModel<RCEntityRenderState> model;
	
	public CarEntityRenderer(Context context)
	{
		super(context);
		this.model = new CarEntityModel(context.bakeLayer(RCToysModClient.MODEL_CAR_LAYER));
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