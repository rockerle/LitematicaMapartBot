package net.rockerle.mapbot.mapbot.client.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.rockerle.mapbot.mapbot.client.MapbotClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxScreen.class)
public class ShulkerBoxScreenMixin extends Screen {

    protected ShulkerBoxScreenMixin(Text text){
        super(text);
    }

    @Inject(at=@At("TAIL"), method="<init>")
    public void onRender(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
        this.addDrawableChild(ButtonWidget.builder(Text.of("add to cache"), button ->{
            System.out.println("pressed button on ShulkerboxContainer");
            MapbotClient.getInvManager().addToStorageCache(
                    MapbotClient.lastInteracted,
                    ((ShulkerBoxScreen)(Object)this).getScreenHandler().inventory
            );
        }).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("remove from cache"), button -> {
            System.out.println("pressed remove button");
            MapbotClient.getInvManager().removeFromStorageCache(MapbotClient.lastInteracted);
        }).position(0,20).build());
    }
}