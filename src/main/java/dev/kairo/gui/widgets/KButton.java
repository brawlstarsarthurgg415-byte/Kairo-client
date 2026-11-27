package dev.kairo.gui.widgets;

import dev.kairo.gui.KairoTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/** Botao com a identidade visual do Kairo (sprite escuro + barra de destaque). */
public class KButton extends ClickableWidget {
    public interface PressAction {
        void onPress(KButton button);
    }

    private final PressAction action;

    public KButton(String label, int x, int y, int w, int h, PressAction action) {
        super(x, y, w, h, Text.literal(label));
        this.action = action;
    }

    public void setLabel(String label) {
        setMessage(Text.literal(label));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        try {
            MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        } catch (Exception ignored) {
            // Sem sistema de som disponivel (ex.: inicializacao minima); o clique continua funcionando.
        }
        if (action != null) action.onPress(this);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = this.active && this.isHovered();
        KairoTheme.button(context, getX(), getY(), getWidth(), getHeight(), hovered, this.active);
        int textColor;
        if (!this.active) {
            textColor = KairoTheme.MUTED;
        } else if (hovered) {
            textColor = KairoTheme.accent();
        } else {
            textColor = KairoTheme.TEXT;
        }
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, getMessage(),
            getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, textColor);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}
