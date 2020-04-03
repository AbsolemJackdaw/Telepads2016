package subaraki.telepads.screen;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketTeleport;
import subaraki.telepads.tileentity.render.RenderEndPortalFrame;
import subaraki.telepads.utility.ClientReferences;
import subaraki.telepads.utility.TelepadEntry;

public class TeleportScreen extends Screen {

    // show entries from current worl, or if selected, from another dimension
    // array only contains entries from selected dimension
    private LinkedList<TelepadEntry> entries = new LinkedList<>();

    private int lookup_dim_id = 0;

    private RenderEndPortalFrame endPortalFrame;

    private int scrollbarscroll = 0;

    TextFieldWidget dimension_indicator;

    protected final List<Widget> unscrollables = Lists.newArrayList();

    protected List<Integer> dimensions_visited = Lists.newArrayList();

    private final boolean is_transmitter_pad;

    final int START_X = 10;
    final int START_Y = 30;
    final int GAP = 5;

    public TeleportScreen(boolean is_transmitter_pad) {

        super(new TranslationTextComponent("telepad.gui"));
        this.is_transmitter_pad = is_transmitter_pad;
        endPortalFrame = new RenderEndPortalFrame();

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

        lookup_dim_id = minecraft.world.dimension.getType().getId();

        scrollbarscroll = 0;
        dimension_indicator = new TextFieldWidget(font, minecraft.mainWindow.getScaledWidth() / 2 - 75, 5, 150, 20, "indicator");
        dimension_indicator.setText(DimensionType.getById(lookup_dim_id).getRegistryName().getPath());

        initialize_pages();

    }

    @Override
    public void render(int mouseX, int mouseY, float p_render_3_)
    {

        endPortalFrame.renderEndPortalSurfaceGUI(0, 0, 0, minecraft, this);

        GlStateManager.disableLighting();

        fill(START_X, START_Y, width - START_X, height - START_Y, 0x0055444444);

        GL11.glColor4f(1, 1, 1, 1);

        MainWindow window = minecraft.mainWindow;
        int scale = (int) window.getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(START_X * scale, START_Y * scale, width * scale, (height - (START_Y * 2)) * scale);

        super.render(mouseX, mouseY, p_render_3_);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (!buttons.isEmpty())
        {
            drawFakeScrollBar();
        }

        GL11.glColor4f(1, 1, 1, 1);

        dimension_indicator.render(mouseX, mouseY, p_render_3_);

        unscrollables.forEach(b -> b.render(mouseX, mouseY, p_render_3_));
        GlStateManager.enableLighting();

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScroll)
    {

        int index = buttons.size() > 0 ? buttons.size() - 1 : 0;
        Widget last = buttons.get(index);
        Widget first = buttons.get(0);

        int forsee_bottom_limit = (int) (last.y + last.getHeight() + (mouseScroll * 16));
        int bottom_limit = height - START_Y - last.getHeight();

        int forsee_top_limit = (int) (first.y - 15 + mouseScroll * 16);
        int top_limit = GAP + START_Y;
        // scrolling up
        if (mouseScroll < 0.0 && forsee_bottom_limit < bottom_limit)
            return super.mouseScrolled(mouseX, mouseY, mouseScroll);
        // down
        if (mouseScroll > 0.0 && forsee_top_limit > top_limit)
            return super.mouseScrolled(mouseX, mouseY, mouseScroll);

        move(mouseScroll);

        return super.mouseScrolled(mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int buttonID, double amountX, double amountY)
    {

        return super.mouseDragged(mouseX, mouseY, buttonID, amountX, amountY);
    }

    @Override
    public void onClose()
    {

        super.onClose();
    }

    private void drawFakeScrollBar()
    {

        int top = buttons.get(0).y;
        int bot = buttons.get(buttons.size() - 1).y + buttons.get(buttons.size() - 1).getHeight();

        // get total size for buttons drawn
        float totalSize = (bot - top) + (GAP);
        float containerSize = height - START_Y * 2;

        // relative % of the scale between the buttons drawn and the screen size
        float percent = (((float) containerSize / (float) totalSize) * 100f);

        if (percent < 100)
        {

            float sizeBar = (containerSize / 100f * percent);

            float relativeScroll = ((float) scrollbarscroll / 100f * percent);

            // what kind of dumbfuck decided it was intelligent to have 'fill' fill in from
            // left to right
            // and fillgradient from right to fucking left ???

            // this.fill(width - START_X, START_Y + (int) relativeScroll, width - START_X -
            // 4, START_Y + (int) relativeScroll + (int) sizeBar,
            // 0xff00ffff);

            // draw a black background background
            this.fillGradient(width - START_X, START_Y, width, START_Y + (int) containerSize, 0x80000000, 0x80222222);
            // Draw scrollbar
            this.fillGradient(width - START_X, START_Y + (int) relativeScroll, width, START_Y + (int) relativeScroll + (int) sizeBar, 0x80ffffff, 0x80222222);

        }
    }

    private void move(double scroll)
    {

        scrollbarscroll -= scroll * 16;

        for (Widget button : this.buttons)
        {

            button.y += scroll * 16;
        }
    }

    private void setup_dimension_page()
    {

        // initialize page for selected dimension

        PlayerEntity player = minecraft.player;
        TelepadData.get(player).ifPresent((data) -> {

            for (TelepadEntry entry : data.getEntries())
            {
                if (entry.dimensionID == lookup_dim_id)
                    entries.add(entry);
            }
        });

        int max_collumns = minecraft.mainWindow.getScaledWidth() / 130;
        int increment = max_collumns;
        int central_offset = (minecraft.mainWindow.getScaledWidth() / 2) - ((max_collumns * 120) / 2);

        for (TelepadEntry entry : entries)
        {

            int extra_y = increment / max_collumns;
            int extra_x = increment % max_collumns;
            ChatFormatting info = entry.isMissingFromLocation ? ChatFormatting.GRAY
                    : entry.isPowered ? ChatFormatting.DARK_RED
                            : entry.hasTransmitter ? ChatFormatting.GREEN : entry.isPublic ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.WHITE;

            addButton(new Button(central_offset + 5 + (extra_x * 120), 15 + (extra_y * 25), 110, 20, info + entry.entryName, (button) -> {

                if (entry.isMissingFromLocation)
                {
                    this.onClose();
                    ClientReferences.openMissingScreen(entry);
                }
                else
                {
                    NetworkHandler.NETWORK.sendToServer(new SPacketTeleport(minecraft.player.getPosition(), entry, false));
                    this.onClose();
                }
            }));

            increment++;

        }

    }

    private int tuner_counter = 0;

    private void add_paging_buttons()
    {

        // set the tuner to the right index for the currently visiting dimension
        while (dimensions_visited.get(tuner_counter) != lookup_dim_id)
            tuner_counter++;

        int centerx = minecraft.mainWindow.getScaledWidth() / 2;
        Widget button_left = new Button(centerx - 75 - 25, 5, 20, 20, "<", (button) -> {
            if (dimensions_visited.size() > 1)
            {
                tuner_counter--;

                if (tuner_counter < 0)
                    tuner_counter = dimensions_visited.size() - 1;

            }

            lookup_dim_id = dimensions_visited.get(tuner_counter);
            dimension_indicator.setText(DimensionType.getById(lookup_dim_id).getRegistryName().getPath());

            initialize_pages();
        });

        Widget button_right = new Button(centerx + 75 + 5, 5, 20, 20, ">", (button) -> {
            if (dimensions_visited.size() > 1)
            {
                tuner_counter++;

                if (tuner_counter >= dimensions_visited.size())
                    tuner_counter = 0;

            }

            lookup_dim_id = dimensions_visited.get(tuner_counter);
            dimension_indicator.setText(DimensionType.getById(lookup_dim_id).getRegistryName().getPath());

            initialize_pages();

        });

        unscrollables.add(button_left);
        unscrollables.add(button_right);
        children.add(button_left);
        children.add(button_right);
    }

    private void initialize_pages()
    {

        buttons.clear();
        children.clear();
        entries.clear();
        unscrollables.clear();
        dimensions_visited.clear();

        setup_dimension_list();

        setup_dimension_page();

        if (is_transmitter_pad)
        {
            add_paging_buttons();
        }
    }

    private void setup_dimension_list()
    {

        PlayerEntity player = minecraft.player;
        TelepadData.get(player).ifPresent((data) -> {
            data.getEntries().forEach(entry -> {
                if (!dimensions_visited.contains(entry.dimensionID))
                    dimensions_visited.add(entry.dimensionID);
            });
        });
    }
}
