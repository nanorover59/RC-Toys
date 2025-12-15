package rctoys.item;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

public record RemoteLinkComponent(UUID uuid, String name)
{
	public static final Codec<RemoteLinkComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				UUIDUtil.AUTHLIB_CODEC.fieldOf("uuid").forGetter(RemoteLinkComponent::uuid),
				Codec.STRING.fieldOf("name").forGetter(RemoteLinkComponent::name)
			)
			.apply(instance, RemoteLinkComponent::new)
	);
	
	public static final StreamCodec<ByteBuf, RemoteLinkComponent> PACKET_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		RemoteLinkComponent::uuid,
		ByteBufCodecs.STRING_UTF8,
		RemoteLinkComponent::name,
		RemoteLinkComponent::new
	);
}