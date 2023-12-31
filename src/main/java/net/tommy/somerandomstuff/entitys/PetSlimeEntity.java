package net.tommy.somerandomstuff.entitys;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PetSlimeEntity extends TameableEntity {
    public PetSlimeEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        PetSlimeEntity petSlimeEntity = (PetSlimeEntity) SomeRandomStuff.PET_SLIME.create(world);
        if (petSlimeEntity != null) {
            UUID uUID = this.getOwnerUuid();
            if (uUID != null) {
                petSlimeEntity.setOwnerUuid(uUID);
                petSlimeEntity.setTamed(true);
            }
        }

        return petSlimeEntity;
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return item.isFood() && item.getFoodComponent().isMeat();
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    protected void initGoals(){
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal(this, SlimeEntity.class, false));
        this.targetSelector.add(3, new UniversalAngerGoal(this, true));
    }

    public static DefaultAttributeContainer.Builder createPetSlimeAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }
}
