package rctoys.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;

public record TrackingPlayerC2SPacket(int entityID, boolean enable) implements CustomPacketPayload
{
	public static final Type<TrackingPlayerC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "controlling_player"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TrackingPlayerC2SPacket> CODEC = StreamCodec.composite(ByteBufCodecs.INT, TrackingPlayerC2SPacket::entityID, ByteBufCodecs.BOOL, TrackingPlayerC2SPacket::enable, TrackingPlayerC2SPacket::new);
	
	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}