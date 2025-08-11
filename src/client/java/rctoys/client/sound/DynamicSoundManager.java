package rctoys.client.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import rctoys.entity.AbstractRCEntity;
import rctoys.network.c2s.MotorSoundS2CPacket;

public class DynamicSoundManager
{
	private static DynamicSoundManager instance;
	private final List<RCMotorSound> activeSounds = new ArrayList<>();

	private DynamicSoundManager()
	{
	}

	public static DynamicSoundManager getInstance()
	{
		if(instance == null)
			instance = new DynamicSoundManager();

		return instance;
	}
	
	public <T extends RCMotorSound> void play(T soundInstance)
	{
		if(this.activeSounds.contains(soundInstance))
			return;

		MinecraftClient client = MinecraftClient.getInstance();
		client.getSoundManager().play(soundInstance);
		this.activeSounds.add(soundInstance);
	}
	
	public Optional<RCMotorSound> getPlayingSoundInstance(SoundEvent soundEvent)
	{
		for(var activeSound : this.activeSounds)
		{
			if(activeSound.getId().equals(soundEvent.id()))
				return Optional.of(activeSound);
		}

		return Optional.empty();
	}
	
	public static void receiveSoundPacket(MotorSoundS2CPacket payload, ClientPlayNetworking.Context context)
	{
		int entityID = payload.entityID();
		boolean enable = payload.enable();
		Identifier soundID = payload.sound();
		MinecraftClient client = context.client();
		
		client.execute(() -> {
			Entity entity = client.world.getEntityById(entityID);
			SoundEvent sound = Registries.SOUND_EVENT.get(soundID);
			
			if(entity != null && entity instanceof AbstractRCEntity)
			{
				DynamicSoundManager instance = getInstance();
				
				if(enable)
					instance.play(new RCMotorSound((AbstractRCEntity) entity, sound));
				else
					instance.getPlayingSoundInstance(sound).ifPresent(s -> client.getSoundManager().stop(s));
			}
		});
	}
}