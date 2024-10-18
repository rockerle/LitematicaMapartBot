package net.rockerle.mapbot.mapbot.client.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.rockerle.mapbot.mapbot.client.MapbotClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin extends Screen {

    private ButtonWidget.PressAction action = new ButtonWidget.PressAction() {
        @Override
        public void onPress(ButtonWidget button) {
            System.out.println("pressed button on GenericContainer");
            MapbotClient.getInvManager().addToStorageCache(
                    MapbotClient.lastInteracted,
                    ((GenericContainerScreen)(Object)this).getScreenHandler().getInventory()
            );
        }
    };
    protected GenericContainerScreenMixin(Text title) {
        super(title);
    }

    @Inject(at=@At("TAIL"), method="<init>")
    public void onInit(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
        this.addDrawableChild(ButtonWidget.builder(Text.of("add to cache"), button ->{
            System.out.println("pressed button on GenericContainer");
            MapbotClient.getInvManager().addToStorageCache(
                    MapbotClient.lastInteracted,
                    ((GenericContainerScreen)(Object)this).getScreenHandler().getInventory()
            );
        }).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("remove from cache"), button -> {
            System.out.println("pressed remove button");
            MapbotClient.getInvManager().removeFromStorageCache(MapbotClient.lastInteracted);
        }).position(0,20).build());
    }
    @Inject(at=@At("TAIL"), method="render")
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
//        this.addDrawableChild(ButtonWidget.builder(Text.of("cache"), action).build());
    }
}