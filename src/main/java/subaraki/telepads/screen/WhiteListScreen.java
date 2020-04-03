package subaraki.telepads.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketAddWhiteListEntry;

public class WhiteListScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Telepads.MODID, "textures/gui/whitelist.png");

    private final int tex_x = 142;
    private final int tex_y = 166;
    private int center_x;
    private int center_y;

    private boolean is_open = false;

    private final String suggestion;

    public WhiteListScreen() {

        super(new TranslationTextComponent("screen.whitelist"));
        suggestion = new TranslationTextComponent("suggest.name").getFormattedText();
    }

    TextFieldWidget textfield;

    @Override
    public boolean isPauseScreen()
    {

        return false;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {

        this.renderBackground();
        this.minecraft.textureManager.bindTexture(BACKGROUND);
        blit(center_x - tex_x / 2, center_y - tex_y / 2, 0, 0, tex_x, tex_y);

        // tiny hack to better control the suggestion text.
        if (!textfield.getText().isEmpty())
            textfield.setSuggestion("");
        else
            textfield.setSuggestion(suggestion);

        super.render(p_render_1_, p_render_2_, p_render_3_);

        for (int i = 0; i < 9; i++)
        {
            font.drawStringWithShadow("-", center_x - 62, center_y - 47 + (i * 13), 0x888888);

        }
        TelepadData.get(minecraft.player).ifPresent(data -> {

            if (!data.getWhitelist().isEmpty())
            {
                int index = 0;
                for (String name : data.getWhitelist().keySet())
                {
                    font.drawStringWithShadow(name, center_x - 54, center_y - 48 + (index++ * 10), 0xeeeeee);
                }

            }

            font.drawStringWithShadow(data.getWhitelist().size() + "/9", center_x + 45, center_y - 50, 0x002222);

            font.drawStringWithShadow("<add,remove>[playername]", center_x - 65, center_y + 69, 0x555555);

        });
    }

    @Override
    public boolean charTyped(char keyCode, int scanCode)
    {
        if (!is_open)
            is_open = true;
        else
        {
            textfield.charTyped(keyCode, scanCode);
            return true;
        }
        return super.charTyped(keyCode, scanCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
    {

       
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
        {
            String command = textfield.getText();
            textfield.setText("");

            NetworkHandler.NETWORK.sendToServer(new SPacketAddWhiteListEntry(command));
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
        }
        
        if (is_open && !super.keyPressed(keyCode, scanCode, p_keyPressed_3_))
        {
            textfield.keyPressed(keyCode, scanCode, p_keyPressed_3_);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    protected void init()
    {

        super.init();
        center_x = minecraft.mainWindow.getScaledWidth() / 2;
        center_y = minecraft.mainWindow.getScaledHeight() / 2;
        textfield = new TextFieldWidget(font, center_x - tex_x / 2 + 5, center_y - tex_y / 2 + 13, 132, 11, "field_name");
        textfield.setSuggestion(suggestion);
        // player names must be between 3 and 16 characters, our command 'remove' is the
        // longest word, space included gives 23 characters max
        textfield.setMaxStringLength(23);
        textfield.setFocused2(true);
        textfield.setEnabled(true);
        addButton(textfield);
    }
}
