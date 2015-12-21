/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 * 
 * @author billythegoat101
 */
public class REFarm extends RuneEvent {
	public REFarm() {
		super();
	}

	@Override
	public void onInit(EntityRune entityRune) {
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.iron_ingot, 8, -1) };
		sacrifice(entityRune, req);

		if (req[0].stackSize > 0 || !this.takeXP(entityRune, 4)) {
			entityRune.fizzle();
			return;
		}
		entityRune.setRenderStar(true);

		int dustID = entityRune.dusts[entityRune.dusts.length / 2][entityRune.dusts[0].length / 2];
		int r = 1;
		int cBase = 0;
		int cRand = 1;

		switch (dustID) {
		case 100:
			r = 1;
			cBase = 0;
			cRand = 2;
			break;

		case 200:
			r = 2;
			cBase = 1;
			cRand = 3;
			break;

		case 300:
			r = 3;
			cBase = 3;
			cRand = 3;
			break;

		case 400:
			r = 4;
			cBase = 4;
			cRand = 5;
			break;
		}
		entityRune.data[0] = r;
		entityRune.data[1] = cBase;
		entityRune.data[2] = cRand;
	}

	@Override
	public void onTick(EntityRune entityRune) {

		if (entityRune.ticksExisted == 0) {

			int r, cBase, cRand;
			r = entityRune.data[0];
			cBase = entityRune.data[1];
			cRand = entityRune.data[2];

			int i = entityRune.getX();
			int j = entityRune.getY();
			int k = entityRune.getZ();

			World world = entityRune.worldObj;
			world.setBlock(i, j - 1, k, Blocks.water,0,3);
			Random rand = new Random();

			ArrayList<Double> locs = new ArrayList<Double>();
			
			for (int di = -r; di <= r; di++) {
				for (int dk = -r; dk <= r; dk++) {
					layer:

					for (int dj = r; dj >= -r; dj--) {
						Block bidt = world.getBlock(di + i, dj + j, dk + k);
						Block bidb = world.getBlock(di + i, dj + j - 1, dk + k);

						if ((bidb == Blocks.dirt
								|| bidb == Blocks.grass
								|| bidb == Blocks.farmland || bidb == Blocks.sand)
								&& (bidt.getMaterial() == Material.air || DustMod.isDust(bidt) || bidt == Blocks.tallgrass)) {
							world.setBlock(i + di, j + dj - 1,
									k + dk, Blocks.farmland,0,3);
							int meta = cBase + rand.nextInt(cRand);

							if (meta > 7) {
								meta = 7;
							}

							world.setBlockToAir(i + di, j + dj,k + dk);
							world.setBlock(i + di, j + dj,
									k + dk, Blocks.wheat, meta,3);
							locs.add(i+di +0.5);
							locs.add((double)j+dj);
							locs.add(k+dk +0.5);
							break layer;
						}
					}
				}
			}

			locs.add((double)i);
			locs.add(j-1D);
			locs.add((double)k);
			world.setBlock(i, j - 1, k, Blocks.water,0,3);
			
			double[] locations = new double[locs.size()];
			for(int d = 0; d < locs.size(); d++){
				locations[d] = locs.get(d);
			}
			DustMod.spawnParticles(entityRune.worldObj, "smoke", locations, 0,0,0, 8, 0.5,0.2,0.5);
		}
		entityRune.fade();
	}
}
