package dustmod.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dustmod.blocks.BlockDust;
import dustmod.blocks.TileEntityDust;
import dustmod.blocks.TileEntityRut;
import dustmod.dusts.DustManager;

/**
 * 4 3 2 5
 */
public class DustBlockRenderers implements ISimpleBlockRenderingHandler {
	
	public static int dustModelID;
	public static int rutModelID;
	
	public int currentRenderer;
	
	public DustBlockRenderers(int currentRenderer) {
		this.currentRenderer = currentRenderer;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess iblockaccess, int i, int j, int k, Block block, int modelId, RenderBlocks renderblocks) {
		
		if (modelId == dustModelID) {
			renderDust(renderblocks, iblockaccess, i, j, k, block);
			return true;
		} else if (modelId == rutModelID) {
			renderRut(renderblocks, iblockaccess, i, j, k, block);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return currentRenderer;
	}
	
	protected boolean renderBlockFire(IBlockAccess blockAccess, BlockFire blockFire, int x, int y, int z, float[] rgb) {
		Tessellator tessellator = Tessellator.instance;
		IIcon iicon = blockFire.getFireIcon(0);
		IIcon iicon1 = blockFire.getFireIcon(1);
		
		tessellator.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);
		tessellator.setBrightness(blockFire.getMixedBrightnessForBlock(blockAccess, x, y, z));
		double d0 = iicon.getMinU();
		double d1 = iicon.getMinV();
		double d2 = iicon.getMaxU();
		double d3 = iicon.getMaxV();
		float f = 0.5F;
		double d5;
		double d6;
		double d7;
		double d8;
		double d9;
		double d10;
		double d11;
		
		double d4 = x + 0.5D + 0.2D;
		d5 = x + 0.5D - 0.2D;
		d6 = z + 0.5D + 0.2D;
		d7 = z + 0.5D - 0.2D;
		d8 = x + 0.5D - 0.3D;
		d9 = x + 0.5D + 0.3D;
		d10 = z + 0.5D - 0.3D;
		d11 = z + 0.5D + 0.3D;
		tessellator.addVertexWithUV(d8, y + f, z + 1, d2, d1);
		tessellator.addVertexWithUV(d4, y + 0, z + 1, d2, d3);
		tessellator.addVertexWithUV(d4, y + 0, z + 0, d0, d3);
		tessellator.addVertexWithUV(d8, y + f, z + 0, d0, d1);
		tessellator.addVertexWithUV(d9, y + f, z + 0, d2, d1);
		tessellator.addVertexWithUV(d5, y + 0, z + 0, d2, d3);
		tessellator.addVertexWithUV(d5, y + 0, z + 1, d0, d3);
		tessellator.addVertexWithUV(d9, y + f, z + 1, d0, d1);
		d0 = iicon1.getMinU();
		d1 = iicon1.getMinV();
		d2 = iicon1.getMaxU();
		d3 = iicon1.getMaxV();
		tessellator.addVertexWithUV(x + 1, y + f, d11, d2, d1);
		tessellator.addVertexWithUV(x + 1, y + 0, d7, d2, d3);
		tessellator.addVertexWithUV(x + 0, y + 0, d7, d0, d3);
		tessellator.addVertexWithUV(x + 0, y + f, d11, d0, d1);
		tessellator.addVertexWithUV(x + 0, y + f, d10, d2, d1);
		tessellator.addVertexWithUV(x + 0, y + 0, d6, d2, d3);
		tessellator.addVertexWithUV(x + 1, y + 0, d6, d0, d3);
		tessellator.addVertexWithUV(x + 1, y + f, d10, d0, d1);
		d4 = x + 0.5D - 0.5D;
		d5 = x + 0.5D + 0.5D;
		d6 = z + 0.5D - 0.5D;
		d7 = z + 0.5D + 0.5D;
		d8 = x + 0.5D - 0.4D;
		d9 = x + 0.5D + 0.4D;
		d10 = z + 0.5D - 0.4D;
		d11 = z + 0.5D + 0.4D;
		tessellator.addVertexWithUV(d8, y + f, z + 0, d0, d1);
		tessellator.addVertexWithUV(d4, y + 0, z + 0, d0, d3);
		tessellator.addVertexWithUV(d4, y + 0, z + 1, d2, d3);
		tessellator.addVertexWithUV(d8, y + f, z + 1, d2, d1);
		tessellator.addVertexWithUV(d9, y + f, z + 1, d0, d1);
		tessellator.addVertexWithUV(d5, y + 0, z + 1, d0, d3);
		tessellator.addVertexWithUV(d5, y + 0, z + 0, d2, d3);
		tessellator.addVertexWithUV(d9, y + f, z + 0, d2, d1);
		d0 = iicon.getMinU();
		d1 = iicon.getMinV();
		d2 = iicon.getMaxU();
		d3 = iicon.getMaxV();
		tessellator.addVertexWithUV(x + 0, y + f, d11, d0, d1);
		tessellator.addVertexWithUV(x + 0, y + 0, d7, d0, d3);
		tessellator.addVertexWithUV(x + 1, y + 0, d7, d2, d3);
		tessellator.addVertexWithUV(x + 1, y + f, d11, d2, d1);
		tessellator.addVertexWithUV(x + 1, y + f, d10, d0, d1);
		tessellator.addVertexWithUV(x + 1, y + 0, d6, d0, d3);
		tessellator.addVertexWithUV(x + 0, y + 0, d6, d2, d3);
		tessellator.addVertexWithUV(x + 0, y + f, d10, d2, d1);
		
		return true;
	}
	
	public boolean renderDust(RenderBlocks renderblocks, IBlockAccess iblock, int i, int j, int k, Block block) {
		int meta = iblock.getBlockMetadata(i, j, k);
		boolean drawHightlight = (meta == 1 || meta == 3);
		final int size = TileEntityDust.SIZE;
		float px = 1F / 16F;
		float cellWidth = 1F / size;
		float h = 0.025F;
		TileEntityDust ted = (TileEntityDust) iblock.getTileEntity(i, j, k);
		float t = 0.02F;
		
		Tessellator tes = Tessellator.instance;
		tes.setBrightness(block.getMixedBrightnessForBlock(iblock, i, j, k));
		
		int[][][] rendArray = ted.getRendArrays();
		int[][] midArray = rendArray[0]; //Actual points 
		int[][] horizArray = rendArray[1]; //horizontal connectors
		int[][] vertArray = rendArray[2]; //vertical connectors
		float bx, bz, bw, bl;
		float[] col;
		float r, g, b;
		
		if (ted.hasFlame()) {
			renderBlockFire(iblock, Blocks.fire, i, j, k, ted.getFlameColor());
		}
		
		float highlightHeight = 0.125f;
		
		for (int x = 0; x < size + 1; x++) {
			for (int z = 0; z < size + 1; z++) {
				float ox = x * cellWidth;
				float oz = z * (cellWidth);
				
				if (midArray[x][z] != 0) {
					if (meta == BlockDust.ACTIVE_DUST || meta == BlockDust.ACTIVATING_DUST) {
						r = 255.0F / 255.0F;
						g = 0F / 255.0F;
						b = 0F / 255.0F;
					} else if (meta == BlockDust.DEAD_DUST) {
						r = 178F / 255.0F;
						g = 178F / 255.0F;
						b = 178F / 255.0F;
					} else {
						col = DustManager.getFloorRenderColor(midArray[x][z]);
						r = col[0];
						g = col[1];
						b = col[2];
					}
					
					bx = ox + px;
					bz = oz + px;
					bw = 2 * px;
					bl = 2 * px;
					
					renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
					renderblocks.renderStandardBlockWithColorMultiplier(block, i, j, k, r, g, b);
					
					if (drawHightlight) {
						if (meta == BlockDust.ACTIVATING_DUST) {
							tes.setColorOpaque_F(1, 1, 1);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, highlightHeight, bz + bl);
						} else {
							tes.setColorOpaque_F(1, 0.68f, 0.68f);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
						}
						tes.setBrightness(15728880);
					}
				}
				
				if (horizArray[x][z] != 0) {
					
					if (meta == BlockDust.ACTIVE_DUST || meta == BlockDust.ACTIVATING_DUST) {
						r = 255.0F / 255.0F;
						g = 0F / 255.0F;
						b = 0F / 255.0F;
					} else if (meta == BlockDust.DEAD_DUST) {
						r = 178F / 255.0F;
						g = 178F / 255.0F;
						b = 178F / 255.0F;
					} else {
						col = DustManager.getFloorRenderColor(horizArray[x][z]);
						r = col[0];
						g = col[1];
						b = col[2];
					}
					
					bx = ox + px;
					bz = oz - px;
					bw = 2 * px;
					bl = 2 * px;
					
					if (z == 0) {
						bz = 0;
						bl = px;
					}
					
					if (z == size) {
						bl = px;
					}
					
					renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
					renderblocks.renderStandardBlockWithColorMultiplier(block, i, j, k, r, g, b);
					
					if (drawHightlight) {
						if (meta == BlockDust.ACTIVATING_DUST) {
							tes.setColorOpaque_F(1, 1, 1);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, highlightHeight, bz + bl);
						} else {
							tes.setColorOpaque_F(1, 0.68f, 0.68f);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
						}
						tes.setBrightness(15728880);
					}
				}
				
				if (vertArray[x][z] != 0) {
					if (meta == BlockDust.ACTIVE_DUST || meta == BlockDust.ACTIVATING_DUST) {
						r = 255.0F / 255.0F;
						g = 0F / 255.0F;
						b = 0F / 255.0F;
					} else if (meta == BlockDust.DEAD_DUST) {
						r = 178F / 255.0F;
						g = 178F / 255.0F;
						b = 178F / 255.0F;
					} else {
						col = DustManager.getFloorRenderColor(vertArray[x][z]);
						r = col[0];
						g = col[1];
						b = col[2];
					}
					
					bx = ox - px;
					bz = oz + px;
					bw = 2 * px;
					bl = 2 * px;
					
					if (x == 0) {
						bx = 0;
						bw = px;
					}
					
					if (x == size) {
						bw = px;
					}
					
					renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
					renderblocks.renderStandardBlockWithColorMultiplier(block, i, j, k, r, g, b);
					
					if (drawHightlight) {
						if (meta == 3) {
							tes.setColorOpaque_F(1, 1, 1);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, highlightHeight, bz + bl);
						} else {
							tes.setColorOpaque_F(1, 0.68f, 0.68f);
							renderblocks.setRenderBounds(bx, t, bz, bx + bw, t + h, bz + bl);
						}
						tes.setBrightness(15728880);
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean renderRut(RenderBlocks rb, IBlockAccess iblock, int i, int j, int k, Block ignored) {
		TileEntityRut ter = (TileEntityRut) iblock.getTileEntity(i, j, k);
		boolean[] rut = ter.ruts;
		
		if (rut == null)
			return false;
		
		Block block = ter.maskBlock;
		
		if (block == null)
			return false;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(iblock, i, j, k));
		
		Block fluid = ter.fluidBlock;
		float bi = 2F / 16F; //baseInset
		float fi = 1F / 16F; //fluidInset
		float cw = 6F / 16F; //cornerWidth
		float rw = 4F / 16F; //rutWidth
		
		rb.renderAllFaces = true;
		rb.field_152631_f = true;
		
		/*
		 * TOP
		 */
		
		// center
		if (!rut[1 + 0 * 3 + 1 * 9] && !rut[1 + 2 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(cw, 0, cw, cw + rw, 1F, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[1 + 0 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(cw, 0, cw, cw + rw, bi, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[1 + 2 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(cw, 1F - bi, cw, cw + rw, 1F, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		// corners
		rb.overrideBlockBounds(0, 1F - cw, 0, cw, 1F, cw);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(1F - cw, 1F - cw, 0, 1F, 1F, cw);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(0, 1F - cw, 1F - cw, cw, 1F, 1F);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(1F - cw, 1F - cw, 1F - cw, 1F, 1F, 1F);
		rb.renderBlockByRenderType(block, i, j, k);
		
		//n
		if (!rut[1 + 2 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(cw, 1f - cw, 1f - cw, cw + rw, 1f, 1f);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//s
		if (!rut[1 + 2 * 3 + 0 * 9]) {
			rb.overrideBlockBounds(cw, 1f - cw, 0F, cw + rw, 1f, cw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//e
		if (!rut[2 + 2 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(1f - cw, 1f - cw, cw, 1f, 1f, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//w
		if (!rut[0 + 2 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(0F, 1f - cw, cw, cw, 1f, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		if (block == Blocks.grass) {
			block = Blocks.dirt;
		}
		
		//Lower corners
		rb.overrideBlockBounds(0, 0, 0, cw, cw, cw);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(1F - cw, 0, 0, 1F, cw, cw);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(0, 0, 1F - cw, cw, cw, 1F);
		rb.renderBlockByRenderType(block, i, j, k);
		
		rb.overrideBlockBounds(1F - cw, 0, 1F - cw, 1F, cw, 1F);
		rb.renderBlockByRenderType(block, i, j, k);
		
		//Base fluid
		if (fluid != null) {
			float ix, iy, iz;
			float iw, ih, il;
			ix = iy = iz = fi;
			iw = ih = il = 1F - fi;
			ter.updateNeighbors();
			if (ter.isNeighborSolid(1, 0, 0)) {
				iw += fi;
			}
			
			if (ter.isNeighborSolid(-1, 0, 0)) {
				ix -= fi;
				//                iw+=fi;
			}
			
			if (ter.isNeighborSolid(0, 1, 0)) {
				ih += fi;
			}
			
			if (ter.isNeighborSolid(0, -1, 0)) {
				iy -= fi;
				//                ih+=fi;
			}
			
			if (ter.isNeighborSolid(0, 0, 1)) {
				il += fi;
			}
			
			if (ter.isNeighborSolid(0, 0, -1)) {
				iz -= fi;
				//                il+=fi;
			}
			
			rb.unlockBlockBounds();
			rb.setRenderBounds(ix, iy, iz, iw, ih, il);
			rb.renderStandardBlock(fluid, i, j, k);
		} else {
			float ix, iy, iz;
			float iw, ih, il;
			ix = iy = iz = bi;
			iw = ih = il = 1F - bi;
			ter.updateNeighbors();
			if (ter.isNeighborSolid(1, 0, 0)) {
				iw += bi;
			}
			
			if (ter.isNeighborSolid(-1, 0, 0)) {
				ix -= bi;
				//                iw+=fi;
			}
			
			if (ter.isNeighborSolid(0, 1, 0)) {
				ih += bi;
			}
			
			if (ter.isNeighborSolid(0, -1, 0)) {
				iy -= bi;
				//                ih+=fi;
			}
			
			if (ter.isNeighborSolid(0, 0, 1)) {
				il += bi;
			}
			
			if (ter.isNeighborSolid(0, 0, -1)) {
				iz -= bi;
				//                il+=fi;
			}
			
			//Base middle
			rb.overrideBlockBounds(ix, iy, iz, iw, ih, il);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//Centers
		
		//x
		if (!rut[0 + 1 * 3 + 1 * 9] && !rut[2 + 1 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(0, cw, cw, 1F, cw + rw, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[0 + 1 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(0, cw, cw, bi, cw + rw, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[2 + 1 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(1F - bi, cw, cw, 1F, cw + rw, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//z
		if (!rut[1 + 1 * 3 + 0 * 9] && !rut[1 + 1 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(cw, cw, 0F, cw + rw, cw + rw, 1F);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[1 + 1 * 3 + 0 * 9]) {
			rb.overrideBlockBounds(cw, cw, 0F, cw + rw, cw + rw, bi);
			rb.renderBlockByRenderType(block, i, j, k);
		} else if (!rut[1 + 1 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(cw, cw, 1F - bi, cw + rw, cw + rw, 1F);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//Bottom
		//n
		if (!rut[1 + 0 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(cw, 0, 1f - cw, cw + rw, cw, 1f);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//s
		if (!rut[1 + 0 * 3 + 0 * 9]) {
			rb.overrideBlockBounds(cw, 0, 0F, cw + rw, cw, cw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//e
		if (!rut[2 + 0 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(1f - cw, 0, cw, 1f, cw, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//w
		if (!rut[0 + 0 * 3 + 1 * 9]) {
			rb.overrideBlockBounds(0F, 0, cw, cw, cw, cw + rw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//Middle
		//nw
		if (!rut[0 + 1 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(0F, cw, 1f - cw, cw, cw + rw, 1f);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//ne
		if (!rut[2 + 1 * 3 + 2 * 9]) {
			rb.overrideBlockBounds(1f - cw, cw, 1f - cw, 1f, cw + rw, 1f);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//sw
		if (!rut[0 + 1 * 3 + 0 * 9]) {
			rb.overrideBlockBounds(0, cw, 0f, cw, cw + rw, cw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		//se
		if (!rut[2 + 1 * 3 + 0 * 9]) {
			rb.overrideBlockBounds(1f - cw, cw, 0f, 1f, cw + rw, cw);
			rb.renderBlockByRenderType(block, i, j, k);
		}
		
		rb.unlockBlockBounds();
		rb.field_152631_f = false;
		rb.renderAllFaces = false;
		
		return true;
	}
}
