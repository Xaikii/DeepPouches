package net.perpetualeve.deeppouches.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlockedSlot extends Slot {

	public BlockedSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
		super(p_40223_, p_40224_, p_40225_, p_40226_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean allowModification(Player p_150652_) {
		return false;
	}
	
	@Override
	public boolean mayPickup(Player p_40228_) {
		return false;
	}
	
	@Override
	public boolean mayPlace(ItemStack p_40231_) {
		return false;
	}

}
