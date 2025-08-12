package rctoys.network.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import rctoys.RCToysMod;

public record MotorSoundS2CPacket(int entityID, boolean enable, Identifier sound) implements CustomPayload
{
	public static final CustomPayload.Id<MotorSoundS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(RCToysMod.MOD_ID, "motor_sound"));
    public static final PacketCodec<RegistryByteBuf, MotorSoundS2CPacket> CODEC = CustomPayload.codecOf(MotorSoundS2CPacket::write, MotorSoundS2CPacket::new);
	
    private MotorSoundS2CPacket(PacketByteBuf buffer)
    {
    	this(buffer.readInt(), buffer.readBoolean(), buffer.readIdentifier());
    }
    
    private void write(PacketByteBuf buffer)
    {
    	buffer.writeInt(entityID);
    	buffer.writeBoolean(enable);
    	buffer.writeIdentifier(sound);
    }
    
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}