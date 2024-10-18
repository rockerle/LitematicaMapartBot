package net.rockerle.mapbot.mapbot.client.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.rockerle.mapbot.mapbot.client.MapbotClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPayerInteractionManagerMixin {

    @Inject(at=@At("HEAD"), method="interactBlock", cancellable = true)
    public void onInteractBlock$MapartBot(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        MapbotClient.lastInteracted = hitResult.getBlockPos();
    }
}