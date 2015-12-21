package dustmod.client;

import java.nio.FloatBuffer;
import java.util.List;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import dustmod.blocks.TileEntityDustTable;
import dustmod.runes.RuneManager;

public class RenderDustTable extends TileEntitySpecialRenderer {
	private ModelDustBook book;
	
	FloatBuffer field_40448_a;
	
	public RenderDustTable() {
		book = new ModelDustBook();
		field_40448_a = GLAllocation.createDirectFloatBuffer(16);
	}
	
	public void render(TileEntityDustTable tedt, double d, double d1, double d2, float f) {
		float f4 = tedt.prevPageFlipping + (tedt.pageFlipping - tedt.prevPageFlipping) * f + 0.25F;
		float f5 = tedt.prevPageFlipping + (tedt.pageFlipping - tedt.prevPageFlipping) * f + 0.75F;
		f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
		f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
		
		if (f4 < 0.0F) {
			f4 = 0.0F;
		}
		
		if (f5 < 0.0F) {
			f5 = 0.0F;
		}
		
		if (f4 > 1.0F) {
			f4 = 1.0F;
		}
		
		if (f5 > 1.0F) {
			f5 = 1.0F;
		}
		
		float f6 = tedt.prevFloating + (tedt.floating - tedt.prevFloating) * f;
		GL11.glPushMatrix();
		float f1 = tedt.ticks + f;
		float f2;
		
		for (f2 = tedt.rotation - tedt.prevRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
		}
		
		for (; f2 < -(float) Math.PI; f2 += ((float) Math.PI * 2F)) {
		}
		
		float f3 = tedt.prevRotation + f2 * f;
		float t1x = (float) d + 0.5F;
		float t1y = (float) d1 + 0.75F;
		float t1z = (float) d2 + 0.5F;
		float t2x = 0F;
		float t2y = 0.2F;// + MathHelper.sin(f1 * 0.1F) * 0.01F;
		float t2z = 0;
		float r1 = (-f3 * 180F) / (float) Math.PI;
		float r2 = 60F;
		float scale = 1F / 7F;
		GL11.glPushMatrix();
		GL11.glTranslatef(t1x, t1y, t1z);
		GL11.glTranslatef(t2x, t2y + 0.062f, t2z);
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(r1, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(r2, 0.0F, 0.0F, 1.0F);
		
		int page = Math.round(tedt.pageFlipping * 2);
		if (page == 0)
			bindTexture(new ResourceLocation("dustmod", "pages/info.png"));
		else
			PageHelper.bindPage(getRunePageName(page));
		
		book.renderPages(null, f1, f4, f5, f6, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glTranslatef(t1x, t1y, t1z);
		GL11.glTranslatef(t2x, t2y, t2z);
		GL11.glRotatef(r1, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(r2, 0.0F, 0.0F, 1.0F);
		bindTexture(new ResourceLocation("dustmod", "textures/book.png"));
		book.render(null, f1, f4, f5, f6, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		render((TileEntityDustTable) tileentity, d, d1, d2, f);
	}
	
	public static String getRunePageName(int page) {
		List<String> names = RuneManager.getNames();
		String rtn = "";
		
		if (page > names.size()) {
			rtn += "null";
		} else if (page == 0) {
			rtn += "info";
		} else {
			rtn += names.get(page - 1);
		}
		return rtn;
	}
}
