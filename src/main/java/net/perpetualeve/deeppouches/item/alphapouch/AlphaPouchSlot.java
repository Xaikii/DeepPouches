package net.perpetualeve.deeppouches.item.alphapouch;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.perpetualeve.deeppouches.DeepPouches;

public class AlphaPouchSlot extends Slot {

	public AlphaPouchSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
		super(p_40223_, p_40224_, p_40225_, p_40226_);
	}
	
	public boolean mayPlace(ItemStack stack) {
		return DeepPouches.alpha_items.contains(stack.getItem()) && stack.getItem().canFitInsideContainerItems();
	}
	
	@Override
	public int getMaxStackSize() {
		return Integer.MAX_VALUE;
	}
}
