package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireworkSword extends UpgradeableSwordItem {
    public FireworkSword(ToolMaterial toolMaterial, Settings settings, int level) {
        super(toolMaterial, settings, level);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean stacksUpdated = FestivityEffect.addStacks(attacker, 1);

        if (stacksUpdated && attacker instanceof PlayerEntity playerAttacker) {
            sendParticlesPacket(playerAttacker, target);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (Screen.hasShiftDown()) switch (this.getLevel()) {
            case 1:
                tooltip.add(Text.literal("Hit mob -> get Festivity stack, max 5 stacks").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("When falling, press jump to do Firework Jump").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("You'll consume 3 stacks to leap into the air.").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Then press shift to plunge and do AoE damage").formatted(Formatting.GRAY));
                break;
            case 2:
                tooltip.add(Text.literal("When plunge, recover 1 Festivity per entity hit").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Max 2 Festivity stacks recovered per plunge").formatted(Formatting.GRAY));
                break;
            case 3:
                tooltip.add(Text.literal("Plunge damage increased by 25%, and is").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("further increased by 15% for each mob hit by it").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Max increase is 100%").formatted(Formatting.GRAY));
                break;
            case 4:
                tooltip.add(Text.literal("For each mob hit by plunge, restore 1 HP").formatted(Formatting.GRAY));
                break;
            case 5:
                tooltip.add(Text.literal("Festivity cap increased to 10 stacks").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Also, Level 2 stack recovery is no longer limited").formatted(Formatting.GRAY));
                break;
        }
    }

    public static boolean heldInHand(LivingEntity entity) {
        return entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FireworkSword;
    }

    public static int getLevel(LivingEntity entity) {
        if (!heldInHand(entity)) {
            return 0;
        }

        return ((FireworkSword) entity.getStackInHand(Hand.MAIN_HAND).getItem()).getLevel();
    }

    private static void sendParticlesPacket(PlayerEntity playerAttacker, LivingEntity target) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(target.getPos().toVector3f());
        buf.writeVector3f(target.getPos().subtract(playerAttacker.getPos()).toVector3f());
        buf.writeDouble(target.getWidth());
        buf.writeDouble(target.getHeight());
        buf.writeInt(FestivityEffect.getStacks(playerAttacker));

        ServerPlayNetworking.send((ServerPlayerEntity) playerAttacker, ModNetworking.FESTIVITY_GAINED_PARTICLES_ID, buf);
    }
}
