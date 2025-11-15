package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;
import java.util.UUID;

public class BirthdayPartyEffect extends StatusEffect {
    public BirthdayPartyEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    // Attribute modifier with a 15% value reduction for movement speed
    private static final EntityAttributeModifier movementSpeedModifier = new EntityAttributeModifier(
            UUID.fromString("1c9fe7e4-8088-494a-892c-fb1bd366b4ea"),
            "birthday_party_movement_speed_modifier",
            -0.15,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    );

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        // If Toy Bow level 4 or higher, reduce movement speed and defence
        if (amplifier > 0) {
            EntityAttributeInstance movementSpeedAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (movementSpeedAttribute != null) {
                movementSpeedAttribute.addPersistentModifier(movementSpeedModifier);
            }

            ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
            defenseData.setScale(defenseData.getScale() / 1.15f);
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // After dying, stop taunting mobs
        if (!entity.isAlive()) {
            clearAffectedEntities(entity);
            return;
        }

        // Every 0.5 seconds
        if (entity.age % 10 == 0) {
            // Find all nearby mobs
            Box box = new Box(entity.getBlockPos()).expand(16, 8, 16);
            List<MobEntity> otherEntities = entity.getWorld().getOtherEntities(entity, box)
                    .stream()
                    .filter(e -> e instanceof MobEntity)
                    .map(e -> (MobEntity) e)
                    .toList();

            // And taunt them
            for (MobEntity otherEntity : otherEntities) {
                otherEntity.setTarget(entity);
            }
        }

        // Visuals & SFX
        if (entity.isAlive() && !entity.getWorld().isClient) {
            sendConfettiParticlesPacket(entity, entity.getStatusEffect(this).getDuration());
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // After effect runs out, stop taunting mobs
        clearAffectedEntities(entity);

        // Reset movement speed and defence
        if (amplifier > 0) {
            EntityAttributeInstance movementSpeedAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (movementSpeedAttribute != null) {
                movementSpeedAttribute.removeModifier(movementSpeedModifier);
            }

            ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
            defenseData.setScale(defenseData.getScale() * 1.15f);
        }

        super.onRemoved(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private void clearAffectedEntities(LivingEntity target) {
        // Find all nearby mobs
        Box box = new Box(target.getBlockPos()).expand(32, 16, 32);
        List<MobEntity> otherEntities = target.getWorld().getOtherEntities(target, box)
                .stream()
                .filter(e -> e instanceof MobEntity)
                .map(e -> (MobEntity) e)
                .toList();

        // Make them forget this target
        for (MobEntity otherEntity : otherEntities) {
            if (otherEntity.getTarget() == target) {
                otherEntity.setTarget(null);
            }
        }
    }

    private void sendConfettiParticlesPacket(LivingEntity entity, int duration) {
        // The amount of particles to spawn this tick
        int roundedRatio = (int) Math.floor(10.0f * (duration - 1) / 200);
        int count = switch (roundedRatio) {
            case 9 -> 8;
            case 8 -> 5;
            case 7 -> 3;
            case 6, 5 -> 2;
            default -> 1;
        };

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(entity.getPos().add(0, entity.getHeight() / 2, 0).toVector3f());
        buf.writeInt(count);

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.CONFETTI_PARTICLES_ID, buf);
        }
    }
}
