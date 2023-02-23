package net.perpetualeve.deeppouches.item.alphapouch;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlphaPouchScreen extends AbstractContainerScreen<AlphaPouchMenu> {
	private static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation(
			"deeppouches:textures/gui/container/pouch.png");
	Random rand = new Random();
	public AlphaPouchScreen(AlphaPouchMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97742_.getItem(p_97742_.selected).getHoverName());
		inventoryLabelY += 31;
		titleLabelY -= 27-menu.offSetCount;
		titleLabelX += 1;
	}

	public void render(PoseStack p_99249_, int p_99250_, int p_99251_, float p_99252_) {
		this.renderBackground(p_99249_);
		super.render(p_99249_, p_99250_, p_99251_, p_99252_);
        this.renderTooltip(p_99249_, p_99250_, p_99251_);
	}

	@Override
	protected void renderBg(PoseStack p_97787_, float p_97788_, int p_97789_, int p_97790_) {
		RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		int slotsCount = getMenu().count;
		
		int p = menu.offSetCount;
		this.blit(p_97787_, i, j-(24-p), 0, 0, 176, 11);
		
		int cond = (int) Math.min(Math.ceil(slotsCount/9d),6);
		int w = 0;
		for(; w<cond;w++) {
			this.blit(p_97787_, i, j -13+(18*w)+p, 0, 11+(18*w), 176, 18);
		}
		this.blit(p_97787_, i, j-24+p+(w)*18+3, 0, 111, 176, 109);
		
		NonNullList<Slot> slots = getMenu().slots;
		
		for(int k = 0; k<slotsCount;k++) {
			Slot slot = slots.get(k);
			this.blit(p_97787_, i+slot.x-1, j+slot.y-1, 176, ((slot.index & k) % 4)*18, 18, 18);
		}
		for(int k = slotsCount; k<slots.size();k++) {
			Slot slot = slots.get(k);
			this.blit(p_97787_, i+slots.get(k).x-1, j+slots.get(k).y-1, 176, ((slot.index & k) % 4)*18, 18, 18);
		}
	}

}
