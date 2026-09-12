package rctoys.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import rctoys.RCToysMod;

public record RemoteControlC2SPacket(float roll, float pitch, float yaw, float throttle) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<RemoteControlC2SPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "remote_control"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoteControlC2SPacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf output, RemoteControlC2SPacket value) {
            output.writeFloat(value.roll);
            output.writeFloat(value.pitch);
            output.writeFloat(value.yaw);
            output.writeFloat(value.throttle);
        }

        @Override
        public RemoteControlC2SPacket decode(RegistryFriendlyByteBuf input) {
            float roll = input.readFloat();
            float pitch = input.readFloat();
            float yaw = input.readFloat();
            float throttle = input.readFloat();
            return new RemoteControlC2SPacket(roll, pitch, yaw, throttle);
        }
    };
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}