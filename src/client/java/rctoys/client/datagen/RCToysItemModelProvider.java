package rctoys.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import rctoys.RCToysMod;

public class RCToysItemModelProvider extends FabricModelProvider {

    public RCToysItemModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateItemWithTintedBaseLayer(RCToysMod.CAR_ITEM, -48340);
        itemModelGenerator.generateItemWithTintedBaseLayer(RCToysMod.PLANE_ITEM, -16201290);
        itemModelGenerator.generateFlatItem(RCToysMod.REMOTE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(RCToysMod.RESONATING_CIRCUIT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(RCToysMod.MOTOR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(RCToysMod.WHEELS, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(RCToysMod.PROPELLER, ModelTemplates.FLAT_ITEM);
    }
}