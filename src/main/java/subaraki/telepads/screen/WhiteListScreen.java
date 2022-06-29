package subaraki.telepads.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketAddWhiteListEntry;
import subaraki.telepads.utility.ClientReferences;

public class WhiteListScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Telepads.MODID, "textures/gui/whitelist.png");

    private final int textureWidth = 142;
    private final int textureHeight = 166;
    private final String suggestion;
    EditBox textfield;
    private int centerX;
    private int centerY;
    private boolean canType = false;

    public WhiteListScreen() {

        super(net.minecraft.network.chat.Component.translatable("screen.whitelist"));
        suggestion = Component.translatable("suggest.name").getString();
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float partialTicks) {

        this.renderBackground(stack);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);

        blit(stack, centerX - textureWidth / 2, centerY - textureHeight / 2, 0, 0, textureWidth, textureHeight);

        // tiny hack to better control the suggestion text.
        if (!textfield.getValue().isEmpty())
            textfield.setSuggestion("");
        else
            textfield.setSuggestion(suggestion);

        super.render(stack, x, y, partialTicks);

        for (int i = 0; i < 9; i++) {
            font.drawShadow(stack, "-", centerX - 62, centerY - 47 + (i * 13), 0x888888);

        }
        TelepadData.get(ClientReferences.getClientPlayer()).ifPresent(data -> {

            if (!data.getWhitelist().isEmpty()) {
                int index = 0;
                for (String name : data.getWhitelist().keySet()) {
                    font.drawShadow(stack, name, centerX - 54, centerY - 48 + (index++ * 10), 0xeeeeee);
                }

            }

            font.drawShadow(stack, data.getWhitelist().size() + "/9", centerX + 45, centerY - 50, 0x002222);

            font.drawShadow(stack, "<add,remove>[playername]", centerX - 65, centerY + 69, 0x555555);

        });
    }

    @Override
    public boolean charTyped(char keyCode, int scanCode) {
        if (!canType)
            canType = true;
        else {
            textfield.charTyped(keyCode, scanCode);
            return true;
        }
        return super.charTyped(keyCode, scanCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {


        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String command = textfield.getValue();
            textfield.setValue("");

            NetworkHandler.NETWORK.sendToServer(new SPacketAddWhiteListEntry(command));
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
        }

        if (canType && !super.keyPressed(keyCode, scanCode, p_keyPressed_3_)) {
            textfield.keyPressed(keyCode, scanCode, p_keyPressed_3_);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    protected void init() {

        super.init();
        centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
        centerY = minecraft.getWindow().getGuiScaledHeight() / 2;
        textfield = new EditBox(font, centerX - textureWidth / 2 + 5, centerY - textureHeight / 2 + 13, 132, 11, Component.literal("field_name"));
        textfield.setSuggestion(suggestion);
        // player names must be between 3 and 16 characters, our command 'remove' is the
        // longest word, space included gives 23 characters max
        textfield.setMaxLength(23);
        textfield.setFocus(true);
        textfield.setEditable(true);
        addRenderableWidget(textfield);
    }
}
