/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import dustmod.DustMod;
import dustmod.runes.EntityRune;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

/**
 *
 * @author billythegoat101
 */
public class EntityAIRuneFollowBaitRune extends EntityAIBase {
	private final double maxRangeSquare = 20.0D * 20.0D;
	private final double minRangeSquare = 4.0D * 4.0D;
	private final double reachRangeSquare = 2.0D * 2.0D;
	private final int maxTicksToReachTarget = 400;
	private static final boolean enableLogs = false;
	
	private EntityCreature entityCreature;
	public EntityRune entityRune;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private float speed;
	private int ticksMove;
	private int ticksTarget;
	
	public EntityAIRuneFollowBaitRune(EntityRune entityRune, EntityCreature entityCreature, float speed) {
		this.entityRune = entityRune;
		this.entityCreature = entityCreature;
		this.speed = speed;
		setMutexBits(1);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (entityRune == null || entityRune.isDead || entityCreature.isDead) {
			entityRune = null;
			return false;
		}
		
		double currentRange = entityCreature.getDistanceSqToEntity(entityRune);
		if (currentRange > maxRangeSquare) {
			if (enableLogs) {
				System.out.println("Too far...");
			}
			entityRune = null;
			return false;
		}
		if (currentRange < minRangeSquare) {
			if (enableLogs) {
				System.out.println("Too close..." + currentRange);
			}
			return false;
		}
		Vec3 vecTarget = RandomPositionGenerator.findRandomTargetBlockTowards(entityCreature, 5, 2, Vec3.createVectorHelper(entityRune.posX, entityRune.posY, entityRune.posZ));
		
		if (vecTarget == null) {
			if (enableLogs) {
				System.out.println("No path...");
			}
			return false;
		} else {
			if (enableLogs) {
				System.out.println("Path found... " + vecTarget);
			}
			movePosX = vecTarget.xCoord;
			movePosY = vecTarget.yCoord;
			movePosZ = vecTarget.zCoord;
			ticksTarget = maxTicksToReachTarget;
			return true;
		}
	}
	
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting() {
		if (entityRune == null || entityRune.isDead || entityCreature.isDead) {
			if (enableLogs) {
				System.out.println("It's dead gym...");
			}
			entityRune = null;
			return false;
		}
		
		double currentRange = entityCreature.getDistanceSq(movePosX, movePosY, movePosZ);
		if (currentRange > maxRangeSquare) {
			if (enableLogs) {
				System.out.println("Too far...");
			}
			entityRune = null;
			return false;
		}
		if (currentRange < reachRangeSquare) {
			if (enableLogs) {
				System.out.println("Target reached..." + currentRange);
			}
			return false;
		}
		
		if (ticksTarget-- <= 0) {
			if (enableLogs) {
				System.out.println("Couldn't reach target..." + ticksTarget);
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		ticksMove = 0;
	}
	
	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		// no operation
	}
	
	/**
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		if (ticksMove-- <= 0) {
			ticksMove = 10;
			entityCreature.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
		}
		
		if (Math.random() < 0.1) {
			DustMod.spawnParticles(entityCreature.worldObj, "smoke", entityCreature.posX, entityCreature.posY + entityCreature.height / 2, entityCreature.posZ, 0, Math.random() * 0.05, 0,
					(int) (Math.random() * 20), 0.75, entityCreature.height / 2, 0.75);
			if (enableLogs) {
				DustMod.spawnParticles(entityCreature.worldObj, "smoke", movePosX, movePosY, movePosZ, 0, Math.random() * 0.05, 0, (int) (Math.random() * 20), 0.50, 0.25, 0.50);
			}
		}
	}
}
