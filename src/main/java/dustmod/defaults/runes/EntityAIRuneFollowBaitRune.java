/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import dustmod.DustMod;
import dustmod.runes.EntityRune;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

/**
 *
 * @author billythegoat101
 */
public class EntityAIRuneFollowBaitRune extends EntityAIBase {
	private EntityCreature entityCreature;
	private EntityRune entityRune;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private float speed;
	
	public EntityAIRuneFollowBaitRune(EntityCreature entityCreature, float speed) {
		this.entityCreature = entityCreature;
		this.speed = speed;
		setMutexBits(1);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		Entity target = entityCreature.getAttackTarget();
		
		if (target == null) {
			return false;
		}
		
		if (target instanceof EntityRune) {
			entityRune = (EntityRune) target;
			movePosX = entityRune.getX();
			movePosY = entityRune.getY();
			movePosZ = entityRune.getZ();
			return true;
		}
		
		return false;
//        if (targetEntity.getDistanceSqToEntity(theEntity) > (double)(field_48331_g * field_48331_g))
//        {
//            return false;
//        }
//
//        Vec3D vec3d = RandomPositionGenerator.func_48620_a(theEntity, 16, 7, Vec3D.createVector(targetEntity.posX, targetEntity.posY, targetEntity.posZ));
//
//        if (vec3d == null)
//        {
//            return false;
//        }
//        else
//        {
//            movePosX = vec3d.xCoord;
//            movePosY = vec3d.yCoord;
//            movePosZ = vec3d.zCoord;
//            return true;
//        }
	}
	
	@Override
	public void updateTask() {
		super.updateTask();
		
		if (!continueExecuting()) {
			entityCreature.getNavigator().clearPathEntity();
			entityCreature.tasks.taskEntries.remove(this);
			return;
		}
		
		entityCreature.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
		
		if (Math.random() < 0.2) {
			DustMod.spawnParticles(entityCreature.worldObj, "smoke",
					entityCreature.posX, entityCreature.posY + entityCreature.height / 2, entityCreature.posZ,
					0, Math.random() * 0.05, 0,
					(int) (Math.random() * 20),
					0.75, entityCreature.height / 2, 0.75);
		}
	}
	
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting() {
		return (entityRune != null && !entityRune.isDead);
	}
	
	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		entityRune = null;
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		entityCreature.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
	}
}
