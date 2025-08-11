package rctoys.item;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

public record RemoteLinkComponent(UUID uuid, String name)
{
	public static final Codec<RemoteLinkComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Uuids.CODEC.fieldOf("uuid").forGetter(RemoteLinkComponent::uuid),
				Codec.STRING.fieldOf("name").forGetter(RemoteLinkComponent::name)
			)
			.apply(instance, RemoteLinkComponent::new)
	);
	
	public static final PacketCodec<ByteBuf, RemoteLinkComponent> PACKET_CODEC = PacketCodec.tuple(
		Uuids.PACKET_CODEC,
		RemoteLinkComponent::uuid,
		PacketCodecs.STRING,
		RemoteLinkComponent::name,
		RemoteLinkComponent::new
	);
}