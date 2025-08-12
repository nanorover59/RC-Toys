package rctoys.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import rctoys.RCToysMod;

public record RemoteControlC2SPacket(int input) implements CustomPayload
{
	public static final CustomPayload.Id<RemoteControlC2SPacket> ID = new CustomPayload.Id<>(Identifier.of(RCToysMod.MOD_ID, "remote_control"));
    public static final PacketCodec<RegistryByteBuf, RemoteControlC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, RemoteControlC2SPacket::input, RemoteControlC2SPacket::new);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}