package rctoys.client.render.entity.state;

import org.joml.Quaternionf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class RCEntityRenderState extends EntityRenderState
{
	public int color;
	public boolean enabled;
	public Quaternionf quaternion;
}