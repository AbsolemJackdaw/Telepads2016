package subaraki.telepads.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketAddTelepadToWorld;
import subaraki.telepads.utility.TelepadEntry;

public class NameTelepadScreen extends Screen {

    private TextFieldWidget textField;
    private int field_width = 150;
    private int field_height = 20;
    private int center_x, center_y;

    private String text_share;
    private String text_confirm_share;
    private String text_negate_share;
    private String enter;
    private String nameYourPad;

    private boolean share = false;
    String sharing = "";

    private boolean should_show_sharing;
    private BlockPos position;

    public NameTelepadScreen(BlockPos position) {

        super(new TranslationTextComponent("name.pick.screen"));

        text_share = new TranslationTextComponent("button.share").getString();
        text_confirm_share = new TranslationTextComponent("confirm.share").getString();
        text_negate_share = new TranslationTextComponent("negate.share").getString();
        enter = new TranslationTextComponent("enter.to.confirm").getString();

        this.position = position;
    }

    @Override
    public boolean isPauseScreen()
    {

        return false;
    }

    @Override
    protected void init()
    {

        super.init();

        center_x = this.width / 2;
        center_y = this.height / 2;

        initTextField();
        ITextComponent translation = new TranslationTextComponent("name.your.telepad").append(new StringTextComponent(" : "));
        nameYourPad = translation.getString();

        initButtons();

    }

    private void initButtons()
    {

        TelepadData.get(minecraft.player).ifPresent(data -> {
            if (!data.getWhitelist().isEmpty())
            {
                should_show_sharing = true;
                addButton(new Button(center_x - 40, center_y + 20, 45, 20, new TranslationTextComponent(text_share), b -> {
                    share = !share;
                }));

            }
        });
    }

    private void initTextField()
    {

        textField = new TextFieldWidget(font, center_x - field_width / 2, center_y - 50, field_width, field_height, new StringTextComponent("field_name"));
        textField.setBordered(true);
        textField.setEditable(true);
        textField.setCanLoseFocus(false);
        textField.setFocus(true);

        ResourceLocation resLoc = this.minecraft.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(this.minecraft.level.getBiome(minecraft.player.blockPosition()));
        
        String biome_name = "biome."+resLoc.getNamespace()+"."+resLoc.getPath(); //biome names are present in lang files under biome.modname.biomename
        TranslationTextComponent biome = new TranslationTextComponent(biome_name);
        
        String format_name = biome.getString().substring(0, Math.min(15, biome.getString().length()));
       
        textField.setValue(format_name);
        textField.setMaxLength(16);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

        this.renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        textField.render(matrixStack, mouseX, mouseY, partialTicks);

        if (should_show_sharing)
            this.sharing = share ? text_confirm_share : text_negate_share;

        font.drawShadow(matrixStack, sharing, center_x - font.width(sharing) / 2 + 30, center_y + 27, 0xafafaf);
        font.drawShadow(matrixStack, enter, center_x - font.width(enter) / 2, center_y, 0xffffff);
        font.drawShadow(matrixStack, nameYourPad + textField.getValue(), center_x - font.width(nameYourPad + textField.getValue()) / 2,
                center_y - field_height, 0xff0000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
    {

        textField.keyPressed(keyCode, scanCode, p_keyPressed_3_);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)
        {

            TelepadData.get(minecraft.player).ifPresent(data -> {

                TelepadEntry telepad_entry = new TelepadEntry(textField.getValue(), minecraft.level.dimension(), position);
                telepad_entry.addUser(minecraft.player.getUUID());

                if (share)
                    data.getWhitelist().values().forEach(entry -> telepad_entry.addUser(entry));

                NetworkHandler.NETWORK.sendToServer(new SPacketAddTelepadToWorld(telepad_entry));

            });

            this.onClose();
            this.removed();

            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    public boolean charTyped(char car, int index)
    {

        textField.charTyped(car, index);

        return super.charTyped(car, index);
    }
}