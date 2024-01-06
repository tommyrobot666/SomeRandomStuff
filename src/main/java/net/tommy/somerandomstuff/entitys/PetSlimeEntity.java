package net.tommy.somerandomstuff.entitys;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PetSlimeEntity extends TameableEntity {
    public float stretch;
    public float targetStretch;
    public float lastStretch;

    private boolean onGroundLastTick;
    private int ticksOnGround;

    public PetSlimeEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(false);
        this.moveControl = new PetSlimeMoveControl(this);
    }

    @Override
    public void setTamed(boolean tamed) {
        if (tamed){
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(16f);
            this.setHealth(16f);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(7.5f);
            this.setHealth(7.5f);
        }
        super.setTamed(tamed);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        PetSlimeEntity petSlimeEntity = SomeRandomStuff.PET_SLIME.create(world);
        petSlimeEntity.setBaby(true);
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
        if (item.isFood()){
            return item.getFoodComponent().isMeat();
        }
        return false;
    }

    @Override
    public EntityView method_48926() { //get world
        return this.getWorld();
    }

    protected void initGoals(){
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new SwimGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, true));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0, 1f));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 0.8f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        List<Class> no_revenge_types = List.of(PetSlimeEntity.class);
        if (this.isTamed()){
            no_revenge_types.add(PlayerEntity.class);
        }

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this,no_revenge_types.toArray(Class[]::new))).setGroupRevenge(no_revenge_types.toArray(Class[]::new)));
        this.targetSelector.add(4, new ActiveTargetGoal(this, SlimeEntity.class, false));
        this.targetSelector.add(5, new UniversalAngerGoal(this, true));
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (target.getType() == SomeRandomStuff.PET_SLIME){
            return false;
        }
        return super.canAttackWithOwner(target, owner);
    }

    public static DefaultAttributeContainer.Builder createPetSlimeAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 7.5).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0);
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_SLIME_JUMP_SMALL;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient()) {
            if (player.getStackInHand(hand).getItem().isFood()) {
                if (player.getStackInHand(hand).getItem().getFoodComponent().isMeat() && !this.isTamed()) {
                    if (!player.getAbilities().creativeMode) {
                        player.getStackInHand(hand).decrement(1);
                    }
                    this.setOwner(player);
                    this.getWorld().sendEntityStatus(this, (byte) 7); //what this does is in tamable entity class, it makes hearts pop up
                    return ActionResult.CONSUME;
                }
            }
            if (player.getStackInHand(hand).isEmpty() && this.isTamed() && this.getOwnerUuid() == player.getUuid()) {
                this.setSitting(!isSitting());
            }
        } else {
            if ((player.getStackInHand(hand).isEmpty() && this.isTamed() && this.getOwnerUuid() == player.getUuid()) || ((player.getStackInHand(hand).isFood() ? player.getStackInHand(hand).getItem().getFoodComponent().isMeat() : false) && !this.isTamed())){
                return ActionResult.CONSUME;
            }
        }
        if (this.isBaby()){
            for (int i = 0; i < 20; i++) {
                this.getWorld().addParticle(ParticleTypes.ITEM_SLIME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.playSound(this.getJumpSound(), this.getSoundVolume(), 2.1f);
            }
        }
        return super.interactMob(player,hand);
    }

    public void tick() {
        if (this.isBaby()){
            this.getWorld().addParticle(SomeRandomStuff.BABY_PET_SLIME, this.getX() + this.getRandom().nextFloat() - 0.5f, this.getY() + this.getRandom().nextFloat(), this.getZ() + this.getRandom().nextFloat() - 0.5f, 0, 0, 0);
        }

        this.stretch += (this.targetStretch - this.stretch) * 0.5F;
        this.lastStretch = this.stretch;
        super.tick();
        if (this.isOnGround() && !this.onGroundLastTick) {
            int i = 1;

            for(int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float g = this.random.nextFloat() * 0.5F + 0.5F;
                float h = MathHelper.sin(f) * (float)i * 0.5F * g;
                float k = MathHelper.cos(f) * (float)i * 0.5F * g;
                this.getWorld().addParticle(this.getParticles(), this.getX() + (double)h, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetStretch = -0.5F;
        } else if (!this.isOnGround() && this.onGroundLastTick) {
            this.targetStretch = 1.0F;
        }

        this.onGroundLastTick = this.isOnGround();
        this.updateStretch();

        if (this.isOnGround()){
            ticksOnGround++;
        } else {
            ticksOnGround = 0;
        }
    }

    protected void updateStretch() {
        this.targetStretch *= 0.6F;
    }
    public SoundEvent getSquishSound() {
        return SoundEvents.ENTITY_SLIME_SQUISH_SMALL;
    }

    public ParticleEffect getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    private static class PetSlimeMoveControl extends MoveControl {
        private final PetSlimeEntity pet_slime;

        public PetSlimeMoveControl(PetSlimeEntity pet_slime) {
            super(pet_slime);
            this.pet_slime = pet_slime;
        }

        public void tick() {
            if (!this.pet_slime.isSitting()) {
                this.entity.headYaw = this.entity.getYaw();
                this.entity.bodyYaw = this.entity.getYaw();
                super.tick();
                if (this.pet_slime.ticksOnGround >= 3) {
                    this.pet_slime.getJumpControl().setActive();
                    this.pet_slime.playSound(this.pet_slime.getJumpSound(), this.pet_slime.getSoundVolume(), 2.1f);
                }
            }
        }
    }
}
