package rctoys.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;

public record MotorSoundS2CPacket(int entityID, boolean enable, Identifier sound) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<MotorSoundS2CPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "motor_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MotorSoundS2CPacket> CODEC = CustomPacketPayload.codec(MotorSoundS2CPacket::write, MotorSoundS2CPacket::new);
	
    private MotorSoundS2CPacket(FriendlyByteBuf buffer)
    {
    	this(buffer.readInt(), buffer.readBoolean(), buffer.readIdentifier());
    }
    
    private void write(FriendlyByteBuf buffer)
    {
    	buffer.writeInt(entityID);
    	buffer.writeBoolean(enable);
    	buffer.writeIdentifier(sound);
    }
    
	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}