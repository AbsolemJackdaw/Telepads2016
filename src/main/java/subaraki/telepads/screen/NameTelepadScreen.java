package subaraki.telepads.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketAddTelepadToWorld;
import subaraki.telepads.utility.ClientReferences;
import subaraki.telepads.utility.TelepadEntry;

public class NameTelepadScreen extends Screen {

    private final int field_width = 150;
    private final int field_height = 20;
    private final String text_share;
    private final String text_confirm_share;
    private final String text_negate_share;
    private final String enter;
    private final BlockPos position;
    String sharing = "";
    private EditBox textField;
    private int center_x, center_y;
    private String nameYourPad;
    private boolean share = false;
    private boolean should_show_sharing;

    public NameTelepadScreen(BlockPos position) {

        super(new TranslatableComponent("name.pick.screen"));

        text_share = new TranslatableComponent("button.share").getString();
        text_confirm_share = new TranslatableComponent("confirm.share").getString();
        text_negate_share = new TranslatableComponent("negate.share").getString();
        enter = new TranslatableComponent("enter.to.confirm").getString();

        this.position = position;
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    @Override
    protected void init() {

        super.init();

        center_x = this.width / 2;
        center_y = this.height / 2;

        initTextField();
        Component translation = new TranslatableComponent("name.your.telepad").append(new TextComponent(" : "));
        nameYourPad = translation.getString();

        initButtons();

    }

    private void initButtons() {

        TelepadData.get(minecraft.player).ifPresent(data -> {
            if (!data.getWhitelist().isEmpty()) {
                should_show_sharing = true;
                this.addRenderableWidget(new Button(center_x - 40, center_y + 20, 45, 20, new TranslatableComponent(text_share), b -> {
                    share = !share;
                }));

            }
        });
    }

    private void initTextField() {

        textField = new EditBox(font, center_x - field_width / 2, center_y - 50, field_width, field_height, new TextComponent("field_name"));
        textField.setBordered(true);
        textField.setEditable(true);
        textField.setCanLoseFocus(false);
        textField.setFocus(true);

        ResourceLocation resLoc = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(Minecraft.getInstance().level.getBiome(Minecraft.getInstance().player.blockPosition()));

        String biome_name = "biome." + resLoc.getNamespace() + "." + resLoc.getPath(); //biome names are present in lang files under biome.modname.biomename
        TranslatableComponent biome = new TranslatableComponent(biome_name);

        String format_name = biome.getString().substring(0, Math.min(15, biome.getString().length()));

        textField.setValue(format_name);
        textField.setMaxLength(16);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

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
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {

        textField.keyPressed(keyCode, scanCode, p_keyPressed_3_);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {

            TelepadData.get(ClientReferences.getClientPlayer()).ifPresent(data -> {

                TelepadEntry telepad_entry = new TelepadEntry(textField.getValue(), Minecraft.getInstance().level.dimension(), position);
                telepad_entry.addUser(ClientReferences.getClientPlayer().getUUID());

                if (share)
                    data.getWhitelist().values().forEach(telepad_entry::addUser);

                NetworkHandler.NETWORK.sendToServer(new SPacketAddTelepadToWorld(telepad_entry));

            });

            this.onClose();
            this.removed();

            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    public boolean charTyped(char car, int index) {

        textField.charTyped(car, index);

        return super.charTyped(car, index);
    }
}