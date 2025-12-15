package rctoys.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;

public record RemoteControlC2SPacket(int input) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<RemoteControlC2SPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "remote_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoteControlC2SPacket> CODEC = StreamCodec.composite(ByteBufCodecs.INT, RemoteControlC2SPacket::input, RemoteControlC2SPacket::new);
	
	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}