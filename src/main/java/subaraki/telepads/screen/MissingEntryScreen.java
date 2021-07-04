package subaraki.telepads.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketRemoveEntry;
import subaraki.telepads.network.server.SPacketTeleport;
import subaraki.telepads.utility.TelepadEntry;

public class MissingEntryScreen extends Screen {

    private TelepadEntry missing_entry;

    private int center_x = 0;
    private int center_y = 0;
    private String information;
    private String teleport_anyway;
    private String forget;

    public MissingEntryScreen(TelepadEntry missing_entry) {

        super(new TranslationTextComponent("gui.missing.entry"));

        information = new TranslationTextComponent("cannot.find.remove").getString();
        teleport_anyway = new TranslationTextComponent("button.teleport").getString();
        forget = new TranslationTextComponent("button.forget").getString();

        this.missing_entry = missing_entry;
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

        this.center_x = minecraft.getWindow().getGuiScaledWidth() / 2;
        this.center_y = minecraft.getWindow().getGuiScaledHeight() / 2;

        int x = 120;
        int y = 20;
        this.addButton(new Button(center_x - x - 10, center_y + y, x, y, new TranslationTextComponent(teleport_anyway), button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketTeleport(minecraft.player.blockPosition(), missing_entry, false));
            this.removed();
            this.onClose();
        }));

        addButton(new Button(center_x + 10, center_y + y, x, y, new TranslationTextComponent(forget), button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketRemoveEntry(missing_entry));
            this.removed();
            this.onClose();
        }));

    }

    @Override
    public void render(MatrixStack stack, int mouse_x, int mouse_y, float partialTicks)
    {

        this.renderBackground(stack);

        super.render(stack, mouse_x, mouse_y, partialTicks);

        int half = font.width(information) / 2;
        font.drawShadow(stack, information, center_x - half, center_y - 30, 0xff99bb);
    }

}
