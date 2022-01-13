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

    private final int textFieldWidth = 150;
    private final int textFieldHeight = 20;
    private final String textShare;
    private final String textConfirmShare;
    private final String textNegateShare;
    private final String textEnter;
    private final BlockPos position;
    protected String sharing = "";
    private EditBox textfieldBox;
    private int centerX, centerY;
    private String textNameYourPad;
    private boolean share = false;
    private boolean showSharing;

    public NameTelepadScreen(BlockPos position) {

        super(new TranslatableComponent("name.pick.screen"));

        textShare = new TranslatableComponent("button.share").getString();
        textConfirmShare = new TranslatableComponent("confirm.share").getString();
        textNegateShare = new TranslatableComponent("negate.share").getString();
        textEnter = new TranslatableComponent("enter.to.confirm").getString();

        this.position = position;
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    @Override
    protected void init() {

        super.init();

        centerX = this.width / 2;
        centerY = this.height / 2;

        initTextField();
        Component translation = new TranslatableComponent("name.your.telepad").append(new TextComponent(" : "));
        textNameYourPad = translation.getString();

        initButtons();

    }

    private void initButtons() {

        TelepadData.get(minecraft.player).ifPresent(data -> {
            if (!data.getWhitelist().isEmpty()) {
                showSharing = true;
                this.addRenderableWidget(new Button(centerX - 40, centerY + 20, 45, 20, new TranslatableComponent(textShare), b -> {
                    share = !share;
                }));

            }
        });
    }

    private void initTextField() {

        textfieldBox = new EditBox(font, centerX - textFieldWidth / 2, centerY - 50, textFieldWidth, textFieldHeight, new TextComponent("field_name"));
        textfieldBox.setBordered(true);
        textfieldBox.setEditable(true);
        textfieldBox.setCanLoseFocus(false);
        textfieldBox.setFocus(true);

        ResourceLocation resLoc = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(Minecraft.getInstance().level.getBiome(Minecraft.getInstance().player.blockPosition()));

        String biome_name = "biome." + resLoc.getNamespace() + "." + resLoc.getPath(); //biome names are present in lang files under biome.modname.biomename
        TranslatableComponent biome = new TranslatableComponent(biome_name);

        String format_name = biome.getString().substring(0, Math.min(15, biome.getString().length()));

        textfieldBox.setValue(format_name);
        textfieldBox.setMaxLength(16);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        textfieldBox.render(matrixStack, mouseX, mouseY, partialTicks);

        if (showSharing)
            this.sharing = share ? textConfirmShare : textNegateShare;

        font.drawShadow(matrixStack, sharing, centerX - font.width(sharing) / 2 + 30, centerY + 27, 0xafafaf);
        font.drawShadow(matrixStack, textEnter, centerX - font.width(textEnter) / 2, centerY, 0xffffff);
        font.drawShadow(matrixStack, textNameYourPad + textfieldBox.getValue(), centerX - font.width(textNameYourPad + textfieldBox.getValue()) / 2,
                centerY - textFieldHeight, 0xff0000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {

        textfieldBox.keyPressed(keyCode, scanCode, p_keyPressed_3_);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {

            TelepadData.get(ClientReferences.getClientPlayer()).ifPresent(data -> {

                TelepadEntry telepad_entry = new TelepadEntry(textfieldBox.getValue(), Minecraft.getInstance().level.dimension(), position);
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

        textfieldBox.charTyped(car, index);

        return super.charTyped(car, index);
    }
}