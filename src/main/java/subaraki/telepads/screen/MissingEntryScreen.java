package subaraki.telepads.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketRemoveEntry;
import subaraki.telepads.network.server.SPacketTeleport;
import subaraki.telepads.utility.TelepadEntry;

public class MissingEntryScreen extends Screen {

    private final TelepadEntry missingEntry;
    private final String textInformation;
    private final String textTeleportAnyway;
    private final String textForget;
    private int centerX = 0;
    private int centerY = 0;

    public MissingEntryScreen(TelepadEntry missing_entry) {

        super(Component.translatable("gui.missing.entry"));

        textInformation = Component.translatable("cannot.find.remove").getString();
        textTeleportAnyway = Component.translatable("button.teleport").getString();
        textForget = Component.translatable("button.forget").getString();

        this.missingEntry = missing_entry;
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    @Override
    protected void init() {

        super.init();

        this.centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
        this.centerY = minecraft.getWindow().getGuiScaledHeight() / 2;

        int x = 120;
        int y = 20;
        this.addRenderableWidget(Button.builder(Component.translatable(textTeleportAnyway), button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketTeleport(minecraft.player.blockPosition(), missingEntry, false));
            this.removed();
            this.onClose();
        }).bounds(centerX - x - 10, centerY + y, x, y).build());

        this.addRenderableWidget(Button.builder(Component.translatable(textForget), button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketRemoveEntry(missingEntry));
            this.removed();
            this.onClose();
        }).bounds(centerX + 10, centerY + y, x, y).build());

    }

    @Override
    public void render(PoseStack stack, int mouse_x, int mouse_y, float partialTicks) {

        this.renderBackground(stack);

        super.render(stack, mouse_x, mouse_y, partialTicks);

        int half = font.width(textInformation) / 2;
        font.drawShadow(stack, textInformation, centerX - half, centerY - 30, 0xff99bb);
    }

}
