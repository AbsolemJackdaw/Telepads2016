package subaraki.telepads.screen;

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

        information = new TranslationTextComponent("cannot.find.remove").getFormattedText();
        teleport_anyway = new TranslationTextComponent("button.teleport").getFormattedText();
        forget = new TranslationTextComponent("button.forget").getFormattedText();

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

        this.center_x = minecraft.mainWindow.getScaledWidth() / 2;
        this.center_y = minecraft.mainWindow.getScaledHeight() / 2;

        int x = 120;
        int y = 20;
        addButton(new Button(center_x - x -10, center_y + y, x, y, teleport_anyway, button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketTeleport(minecraft.player.getPosition(), missing_entry, false));
            this.onClose();
        }));

        addButton(new Button(center_x +10, center_y + y, x, y, forget, button -> {
            NetworkHandler.NETWORK.sendToServer(new SPacketRemoveEntry(missing_entry));
            this.onClose();
        }));

    }

    @Override
    public void render(int mouse_x, int mouse_y, float p_render_3_)
    {

        this.renderBackground();

        super.render(mouse_x, mouse_y, p_render_3_);

        int half = font.getStringWidth(information) / 2;
        font.drawStringWithShadow(information, center_x - half, center_y - 30, 0xff99bb);
    }

}
