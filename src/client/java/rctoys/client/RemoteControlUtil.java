package rctoys.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import rctoys.entity.AbstractRCEntity;
import rctoys.network.c2s.RemoteControlC2SPacket;

public class RemoteControlUtil {
    public static float previousRoll = 0.0f;
    public static float previousPitch = 0.0f;
    public static float previousYaw = 0.0f;
    public static float previousThrottle = 0.0f;

    public static void control(Options options, AbstractRCEntity entity) {
        KeyMapping.setAll(); // Force a refresh of keys pressed.

        // Input Variables
        float roll = 0.0f;
        float pitch = 0.0f;
        float yaw = 0.0f;
        float throttle = 0.0f;

        roll -= keyPress(options.keyDrop);
        roll += keyPress(options.keyInventory);

        pitch -= keyPress(options.keyUp);
        pitch += keyPress(options.keyDown);

        yaw -= keyPress(options.keyLeft);
        yaw += keyPress(options.keyRight);

        throttle -= keyPress(options.keyShift);
        throttle += keyPress(options.keyJump);

        if(roll != previousRoll
        || pitch != previousPitch
        || yaw != previousYaw
        || throttle != previousThrottle)
            ClientPlayNetworking.send(new RemoteControlC2SPacket(roll, pitch, yaw, throttle));

        previousRoll = roll;
        previousPitch = pitch;
        previousYaw = yaw;
        previousThrottle = throttle;
    }

    private static float keyPress(KeyMapping key) {
        boolean down = key.isDown();
        key.setDown(false);
        while(key.consumeClick());
        return down ? 1.0f : 0.0f;
    }
}