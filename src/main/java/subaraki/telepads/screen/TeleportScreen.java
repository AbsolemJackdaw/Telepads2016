package subaraki.telepads.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.server.SPacketTeleport;
import subaraki.telepads.utility.ClientReferences;
import subaraki.telepads.utility.TelepadEntry;

import java.util.LinkedList;
import java.util.List;

public class TeleportScreen extends Screen {

    protected final List<AbstractWidget> unscrollables = Lists.newArrayList();
    final int startX = 10;
    final int startY = 30;
    final int gap = 5;
    // show entries from current worl, or if selected, from another dimension
    // array only contains entries from selected dimension
    private final LinkedList<TelepadEntry> entries = new LinkedList<>();
    private final boolean isTransmitter;
    protected List<ResourceKey<Level>> visitedList = Lists.newArrayList();
    EditBox dimensionIndicator;
    private ResourceKey<Level> dimensionId = Level.OVERWORLD;
    private int scrollbarscroll = 0;
    private int tunerPosition = 0;

    public TeleportScreen(boolean is_transmitter_pad) {

        super(new TranslatableComponent("telepad.gui"));
        this.isTransmitter = is_transmitter_pad;

    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    @Override
    protected void init() {

        super.init();

        dimensionId = minecraft.level.dimension();

        scrollbarscroll = 0;
        dimensionIndicator = new EditBox(font, minecraft.getWindow().getGuiScaledWidth() / 2 - 75, 5, 150, 20,
                new TranslatableComponent("indicator"));
        dimensionIndicator.setValue(dimensionId.location().getPath());

        initialize_pages();

    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null)
            return;

        stack.pushPose();
        RenderSystem.setShader(GameRenderer::getRendertypeEndPortalShader);
        MultiBufferSource.BufferSource source = minecraft.renderBuffers().bufferSource();
        this.renderCube(stack, source.getBuffer(RenderType.endPortal()));
        //get buffer , draw in it, and END it. if you dont end the buffer, nothing gets drawn and the data gets wiped on next frame draw.
        source.endBatch();
        RenderSystem.disableTexture();
        stack.popPose();

        fill(stack, startX, startY, width - startX, height - startY, 0x0055444444);

        Window window = minecraft.getWindow();
        int scale = (int) window.getGuiScale();
        RenderSystem.enableScissor(startX * scale, startY * scale, width * scale, (height - (startY * 2)) * scale);
        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableScissor();

        if (!this.renderables.isEmpty()) {
            drawFakeScrollBar(stack);
        }

        dimensionIndicator.render(stack, mouseX, mouseY, partialTicks);

        unscrollables.forEach(b -> b.render(stack, mouseX, mouseY, partialTicks));

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScroll) {

        int index = renderables.size() > 0 ? renderables.size() - 1 : 0;

        if (renderables.get(index) instanceof AbstractWidget last && renderables.get(0) instanceof AbstractWidget first) {

            int forsee_bottom_limit = (int) (last.y + last.getHeight() + (mouseScroll * 16));
            int bottom_limit = height - startY - last.getHeight();

            int forsee_top_limit = (int) (first.y - 15 + mouseScroll * 16);
            int top_limit = gap + startY;
            // scrolling up
            if (mouseScroll < 0.0 && forsee_bottom_limit < bottom_limit)
                return super.mouseScrolled(mouseX, mouseY, mouseScroll);
            // down
            if (mouseScroll > 0.0 && forsee_top_limit > top_limit)
                return super.mouseScrolled(mouseX, mouseY, mouseScroll);

            move(mouseScroll);
        }
        return super.mouseScrolled(mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int buttonID, double amountX, double amountY) {

        return super.mouseDragged(mouseX, mouseY, buttonID, amountX, amountY);
    }

    @Override
    public void removed() {

        super.removed();
    }

    private void drawFakeScrollBar(PoseStack stack) {

        if (renderables.get(renderables.size() - 1) instanceof AbstractWidget last && renderables.get(0) instanceof AbstractWidget first) {
            int top = first.y;
            int bot = last.y + last.getHeight();

            // get total size for buttons drawn
            float totalSize = (bot - top) + (gap);
            float containerSize = height - startY * 2;

            // relative % of the scale between the buttons drawn and the screen size
            float percent = ((containerSize / totalSize) * 100f);

            if (percent < 100) {

                float sizeBar = (containerSize / 100f * percent);

                float relativeScroll = ((float) scrollbarscroll / 100f * percent);

                // what kind of dumbfuck decided it was intelligent to have 'fill' fill in from
                // left to right
                // and fillgradient from right to fucking left ???

                // this.fill(width - START_X, START_Y + (int) relativeScroll, width - START_X -
                // 4, START_Y + (int) relativeScroll + (int) sizeBar,
                // 0xff00ffff);

                // draw a black background background
                this.fillGradient(stack, width - startX, startY, width, startY + (int) containerSize, 0x80000000, 0x80222222);
                // Draw scrollbar
                this.fillGradient(stack, width - startX, startY + (int) relativeScroll, width, startY + (int) relativeScroll + (int) sizeBar, 0x80ffffff,
                        0x80222222);
            }
        }
    }

    private void move(double scroll) {

        scrollbarscroll -= scroll * 16;

        for (Widget widget : this.renderables) {
            if (widget instanceof AbstractWidget button)
                button.y += scroll * 16;
        }
    }

    private void setup_dimension_page() {

        // initialize page for selected dimension

        TelepadData.get(ClientReferences.getClientPlayer()).ifPresent((data) -> {

            for (TelepadEntry entry : data.getEntries()) {
                if (entry.dimensionID.equals(dimensionId))
                    entries.add(entry);
            }
        });

        int max_collumns = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 130;
        int increment = max_collumns;
        int central_offset = (Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2) - ((max_collumns * 120) / 2);

        for (TelepadEntry entry : entries) {

            int extra_y = increment / max_collumns;
            int extra_x = increment % max_collumns;
            ChatFormatting color =
                    entry.isMissingFromLocation ? ChatFormatting.GRAY
                            : entry.isPowered ? ChatFormatting.DARK_RED
                            : entry.isPublic ? ChatFormatting.LIGHT_PURPLE
                            : entry.hasTransmitter ? ChatFormatting.GREEN
                            : ChatFormatting.WHITE;
            addRenderableWidget(new Button(central_offset + 5 + (extra_x * 120), 15 + (extra_y * 25), 110, 20,
                    new TextComponent(entry.entryName).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(color)).
                            withBold(entry.position.equals(ClientReferences.getClientPlayer().blockPosition()))), (button) -> {

                if (entry.isMissingFromLocation) {
                    this.removed();
                    this.onClose();
                    ClientReferences.openMissingScreen(entry);
                } else {
                    NetworkHandler.NETWORK.sendToServer(new SPacketTeleport(ClientReferences.getClientPlayer().blockPosition(), entry, false));
                    this.removed();
                    this.onClose();
                }
            }));

            increment++;

        }

    }

    private void add_paging_buttons() {

        // set the tuner to the right index for the currently visiting dimension
        while (visitedList.get(tunerPosition) != dimensionId)
            tunerPosition++;

        int centerx = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
        AbstractWidget button_left = new Button(centerx - 75 - 25, 5, 20, 20, new TextComponent("<"), (button) -> {
            if (visitedList.size() > 1) {
                tunerPosition--;

                if (tunerPosition < 0)
                    tunerPosition = visitedList.size() - 1;

            }

            dimensionId = visitedList.get(tunerPosition);
            dimensionIndicator.setValue(dimensionId.location().getPath());

            initialize_pages();
        });

        AbstractWidget button_right = new Button(centerx + 75 + 5, 5, 20, 20, new TextComponent(">"), (button) -> {
            if (visitedList.size() > 1) {
                tunerPosition++;

                if (tunerPosition >= visitedList.size())
                    tunerPosition = 0;

            }

            dimensionId = visitedList.get(tunerPosition);
            dimensionIndicator.setValue(dimensionId.location().getPath());

            initialize_pages();

        });

        unscrollables.add(button_left);
        unscrollables.add(button_right);
        //add the left and right buttons as widgets and render them
        //in the render method from the unscrollables list.
        //renderables are used to be scrolled up and down.
        addWidget(button_left);
        addWidget(button_right);
    }

    private void initialize_pages() {

        this.clearWidgets();
        entries.clear();
        unscrollables.clear();
        visitedList.clear();

        setup_dimension_list();

        setup_dimension_page();

        if (isTransmitter) {
            add_paging_buttons();
        }
    }

    private void setup_dimension_list() {

        TelepadData.get(ClientReferences.getClientPlayer()).ifPresent((data) -> {
            data.getEntries().forEach(entry -> {
                if (!visitedList.contains(entry.dimensionID))
                    visitedList.add(entry.dimensionID);
            });
        });
    }

    private void renderCube(PoseStack stack, VertexConsumer vertexConsumer) {
        float width = (float) Minecraft.getInstance().getWindow().getGuiScaledWidth();
        float height = (float) Minecraft.getInstance().getWindow().getGuiScaledHeight();
        float max = Math.max(width, height);
        this.renderFace(stack, vertexConsumer, 0.0f, max, max, 0.0f, 0.0F, 0.0F, 0.0F, 0.0F);

    }

    private void renderFace(PoseStack stack, VertexConsumer vertexConsumer, float x0, float x1, float y0, float y1, float z1, float z2, float z3, float z4) {
        Matrix4f matrix4f = stack.last().pose();
        vertexConsumer.vertex(matrix4f, x0, y0, z1).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y0, z2).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y1, z3).endVertex();
        vertexConsumer.vertex(matrix4f, x0, y1, z4).endVertex();
    }
}
