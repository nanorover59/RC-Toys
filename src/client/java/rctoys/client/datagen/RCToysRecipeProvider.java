package rctoys.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import rctoys.RCToysMod;

import java.util.concurrent.CompletableFuture;

public class RCToysRecipeProvider extends FabricRecipeProvider {
    public RCToysRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                this.shaped(RecipeCategory.MISC, RCToysMod.CAR_ITEM)
                        .pattern("aaa")
                        .pattern("bcb")
                        .define('a', Items.IRON_INGOT)
                        .define('b', RCToysMod.WHEELS)
                        .define('c', RCToysMod.RESONATING_CIRCUIT)
                        .unlockedBy(this.getHasName(RCToysMod.RESONATING_CIRCUIT), has(RCToysMod.RESONATING_CIRCUIT))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.PLANE_ITEM)
                        .pattern(" a ")
                        .pattern("cbc")
                        .pattern(" c ")
                        .define('a', RCToysMod.PROPELLER)
                        .define('b', RCToysMod.RESONATING_CIRCUIT)
                        .define('c', ItemTags.PLANKS)
                        .unlockedBy(this.getHasName(RCToysMod.RESONATING_CIRCUIT), has(RCToysMod.RESONATING_CIRCUIT))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.REMOTE)
                        .pattern("ca ")
                        .pattern("aba")
                        .define('a', Items.IRON_INGOT)
                        .define('b', RCToysMod.RESONATING_CIRCUIT)
                        .define('c', Items.REDSTONE_TORCH)
                        .unlockedBy(this.getHasName(RCToysMod.RESONATING_CIRCUIT), has(RCToysMod.RESONATING_CIRCUIT))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.RESONATING_CIRCUIT)
                        .pattern("cbc")
                        .pattern("aaa")
                        .define('a', Items.LAPIS_LAZULI)
                        .define('b', Items.AMETHYST_SHARD)
                        .define('c', Items.REDSTONE)
                        .unlockedBy(this.getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                        .unlockedBy(this.getHasName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.MOTOR)
                        .pattern(" a ")
                        .pattern("bcb")
                        .pattern(" a ")
                        .define('a', Items.IRON_INGOT)
                        .define('b', Items.COPPER_INGOT)
                        .define('c', Items.REDSTONE)
                        .unlockedBy(this.getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                        .unlockedBy(this.getHasName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.WHEELS)
                        .pattern("c c")
                        .pattern("bab")
                        .pattern("c c")
                        .define('a', RCToysMod.MOTOR)
                        .define('b', Items.IRON_INGOT)
                        .define('c', Items.BLACK_DYE)
                        .unlockedBy(this.getHasName(RCToysMod.MOTOR), has(RCToysMod.MOTOR))
                        .save(output);
                this.shaped(RecipeCategory.MISC, RCToysMod.PROPELLER)
                        .pattern(" c ")
                        .pattern("bab")
                        .pattern(" c ")
                        .define('a', RCToysMod.MOTOR)
                        .define('b', Items.IRON_INGOT)
                        .define('c', Items.STICK)
                        .unlockedBy(this.getHasName(RCToysMod.MOTOR), has(RCToysMod.MOTOR))
                        .save(output);

                this.dyedItem(RCToysMod.CAR_ITEM, "dyed_rc_car");
                this.dyedItem(RCToysMod.PLANE_ITEM, "dyed_rc_plane");
            }
        };
    }

    @Override
    public String getName() {
        return "RCToysRecipeProvider";
    }
}
