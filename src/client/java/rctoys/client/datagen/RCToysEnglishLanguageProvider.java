package rctoys.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import rctoys.RCToysMod;

import java.util.concurrent.CompletableFuture;

public class RCToysEnglishLanguageProvider extends FabricLanguageProvider {
    protected RCToysEnglishLanguageProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Items
        translationBuilder.add(RCToysMod.CAR_ITEM, "RC Car");
        translationBuilder.add(RCToysMod.PLANE_ITEM, "RC Plane");
        translationBuilder.add(RCToysMod.REMOTE, "Remote Controller");
        translationBuilder.add(RCToysMod.RESONATING_CIRCUIT, "Resonating Circuit");
        translationBuilder.add(RCToysMod.MOTOR, "Motor");
        translationBuilder.add(RCToysMod.WHEELS, "Wheels");
        translationBuilder.add(RCToysMod.PROPELLER, "Propeller");
        // Entity Types
        translationBuilder.add(RCToysMod.CAR, "RC Car");
        translationBuilder.add(RCToysMod.PLANE, "RC Plane");
        // Sound Events
        translationBuilder.add(RCToysMod.REMOTE_LINK_SOUND, "Remote Linked");
        translationBuilder.add(RCToysMod.CAR_LOOP_SOUND, "RC Car");
        translationBuilder.add(RCToysMod.PLANE_LOOP_SOUND, "RC Plane");
        // Other
        translationBuilder.add("rctoys.itemGroup", "RC Toys");
        translationBuilder.add("item.rctoys.remote_linked", "Linked to %s");
        translationBuilder.add("item.rctoys.started_controlling", "Started controlling %s");
        translationBuilder.add("item.rctoys.stopped_controlling", "Stopped controlling %s");
        translationBuilder.add("entity.rctoys.remote_linked", "Remote link established");
    }
}