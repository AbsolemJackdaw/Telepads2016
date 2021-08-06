package subaraki.telepads.utility;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class PropertiesWrapper {

    public static net.minecraft.world.item.Item.Properties getItemProperties() {

        return new net.minecraft.world.item.Item.Properties();
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties createBlockProperty(Material material) {

        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(material);
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties createBlockProperty(Material material, DyeColor color) {

        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(material, color);
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties createBlockProperty(Material material, MaterialColor color) {

        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(material, color);
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties fromBlockProperty(Block block) {

        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(block);
    }
}
