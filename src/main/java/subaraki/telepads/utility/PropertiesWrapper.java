package subaraki.telepads.utility;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;

public class PropertiesWrapper {

    public static net.minecraft.item.Item.Properties getItemProperties() {

        return new net.minecraft.item.Item.Properties();
    }

    public static net.minecraft.block.Block.Properties createBlockProperty(Material material) {

        return net.minecraft.block.Block.Properties.create(material);
    }

    public static net.minecraft.block.Block.Properties createBlockProperty(Material material, DyeColor color) {

        return net.minecraft.block.Block.Properties.create(material, color);
    }

    public static net.minecraft.block.Block.Properties createBlockProperty(Material material, MaterialColor color) {

        return net.minecraft.block.Block.Properties.create(material, color);
    }

    public static net.minecraft.block.Block.Properties fromBlockProperty(Block block) {

        return net.minecraft.block.Block.Properties.from(block);
    }
}
