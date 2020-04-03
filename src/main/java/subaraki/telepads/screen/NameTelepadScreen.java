package subaraki.telepads.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
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

        text_share = new TranslationTextComponent("button.share").getFormattedText();
        text_confirm_share = new TranslationTextComponent("confirm.share").getFormattedText();
        text_negate_share = new TranslationTextComponent("negate.share").getFormattedText();
        enter = new TranslationTextComponent("enter.to.confirm").getFormattedText();

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
        nameYourPad = new TranslationTextComponent("name.your.telepad").getFormattedText() + " : " + textField.getText();

        initButtons();

    }

    private void initButtons()
    {

        TelepadData.get(minecraft.player).ifPresent(data -> {
            if (!data.getWhitelist().isEmpty())
            {
                should_show_sharing = true;
                this.addButton(new Button(center_x - 40, center_y + 20, 45, 20, text_share, (Button) -> {
                    share = !share;
                }));
            }
        });
    }

    private void initTextField()
    {

        textField = new TextFieldWidget(font, center_x - field_width / 2, center_y - 50, field_width, field_height, "field_name");
        textField.setEnableBackgroundDrawing(true);
        textField.setEnabled(true);
        textField.setCanLoseFocus(false);
        textField.setFocused2(true);
        String biome_name = this.minecraft.world.getBiome(this.minecraft.player.getPosition()).getDisplayName().getFormattedText();
        String format_name = biome_name.substring(0, Math.min(15, biome_name.length()));
        textField.setText(format_name);
        textField.setMaxStringLength(16);
    }

    @Override
    public void render(int mouseX, int mouseY, float p_render_3_)
    {

        this.renderBackground();

        super.render(mouseX, mouseY, p_render_3_);

        textField.render(mouseX, mouseY, p_render_3_);

        if (should_show_sharing)
            this.sharing = share ? text_confirm_share : text_negate_share;

        font.drawStringWithShadow(sharing, center_x - font.getStringWidth(sharing) / 2 + 30, center_y + 27, 0xafafaf);
        font.drawStringWithShadow(enter, center_x - font.getStringWidth(enter) / 2, center_y, 0xffffff);
        font.drawStringWithShadow(nameYourPad, center_x - font.getStringWidth(nameYourPad) / 2, center_y - field_height, 0xff0000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
    {

        textField.keyPressed(keyCode, scanCode, p_keyPressed_3_);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)
        {

            TelepadData.get(minecraft.player).ifPresent(data -> {

                TelepadEntry telepad_entry = new TelepadEntry(textField.getText(), minecraft.world.dimension.getType().getId(), position);
                telepad_entry.addUser(minecraft.player.getUniqueID());

                if (share)
                    data.getWhitelist().values().forEach(entry -> telepad_entry.addUser(entry));

                NetworkHandler.NETWORK.sendToServer(new SPacketAddTelepadToWorld(telepad_entry));

            });
            this.onClose();
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