package net.freedinner.satisfying_weapons.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class UpgraderScreen extends ForgingScreen<UpgraderScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
    private final PlayerEntity player;

    public UpgraderScreen(UpgraderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);

        this.player = inventory.player;
        this.titleX = 60;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.player.closeHandledScreen();
        }

        return true;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
        context.drawTexture(TEXTURE, this.x + 59, this.y + 20, 0, this.backgroundHeight + (this.handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            context.drawTexture(TEXTURE, x + 99, y + 45, this.backgroundWidth, 0, 28, 21);
        }
    }
}
