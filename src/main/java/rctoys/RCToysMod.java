package rctoys;

import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import rctoys.entity.AbstractRCEntity;
import rctoys.entity.CarEntity;
import rctoys.entity.PlaneEntity;
import rctoys.item.RCToyItem;
import rctoys.item.RemoteItem;
import rctoys.item.RemoteLinkComponent;
import rctoys.network.c2s.MotorSoundS2CPacket;
import rctoys.network.c2s.RemoteControlC2SPacket;

public class RCToysMod implements ModInitializer
{
	public static final String MOD_ID = "rctoys";

	public static final EntityType<CarEntity> CAR = registerEntity("rc_car", EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC).dimensions(0.4f, 0.25f).eyeHeight(0.15F).maxTrackingRange(32));
	public static final EntityType<PlaneEntity> PLANE = registerEntity("rc_plane", EntityType.Builder.create(PlaneEntity::new, SpawnGroup.MISC).dimensions(0.75f, 0.25f).eyeHeight(0.15F).maxTrackingRange(32));


	public static final Item REMOTE = registerItem("remote", settings -> new RemoteItem(settings));
	public static final Item CAR_ITEM = registerItem("rc_car", settings -> new RCToyItem(CAR, settings));
	public static final Item PLANE_ITEM = registerItem("rc_plane", settings -> new RCToyItem(PLANE, settings));
	public static final Item RESONATING_CIRCUIT = registerItem("resonating_circuit", settings -> new Item(settings));
	public static final Item MOTOR = registerItem("motor", settings -> new Item(settings));
    public static final Item WHEELS = registerItem("wheels", settings -> new Item(settings));
    public static final Item PROPELLER = registerItem("propeller", settings -> new Item(settings));
    public static final Item AERO_SURFACE = registerItem("aero_surface", settings -> new Item(settings));

	public static final ItemGroup RC_TOYS_ITEM_GROUP = registerItemGroup("rc_toys", CAR_ITEM);

	public static final ComponentType<RemoteLinkComponent> REMOTE_LINK = registerItemComponent("remote_link", builder -> builder.codec(RemoteLinkComponent.CODEC).packetCodec(RemoteLinkComponent.PACKET_CODEC));

	public static final SoundEvent REMOTE_LINK_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "remote_link"), SoundEvent.of(Identifier.of(MOD_ID, "remote_link")));
	public static final SoundEvent CAR_LOOP_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "car_loop"), SoundEvent.of(Identifier.of(MOD_ID, "car_loop")));
    public static final SoundEvent PLANE_LOOP_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "plane_loop"), SoundEvent.of(Identifier.of(MOD_ID, "plane_loop")));

	@Override
	public void onInitialize()
	{
		PayloadTypeRegistry.playS2C().register(MotorSoundS2CPacket.ID, MotorSoundS2CPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(RemoteControlC2SPacket.ID, RemoteControlC2SPacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(RemoteControlC2SPacket.ID, (payload, context) -> AbstractRCEntity.receiveControl(payload, context));
	}

	private static <T extends Entity> EntityType<T> registerEntity(String id, EntityType.Builder<T> type)
	{
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, id));
		EntityType<T> entityType = Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
		return entityType;
	}

	public static Item registerItem(String id, Function<Item.Settings, Item> factory)
	{
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, id));
		Item item = factory.apply(new Item.Settings().registryKey(key));
		return Registry.register(Registries.ITEM, key, item);
	}

	private static <T> ComponentType<T> registerItemComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator)
	{
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, id), builderOperator.apply(ComponentType.builder()).build());
	}

    public static ItemGroup registerItemGroup(String id, ItemConvertible icon)
    {
        RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, id));
        ItemGroup.EntryCollector collector = (displayContext, entries) -> Registries.ITEM.forEach(item -> {
            if(Registries.ITEM.getId(item).getNamespace().equals(MOD_ID))
                entries.add(item);
        });
        ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(icon)).displayName(Text.translatable("rctoys.itemGroup")).entries(collector).build();
        return Registry.register(Registries.ITEM_GROUP, key, itemGroup);
    }
}