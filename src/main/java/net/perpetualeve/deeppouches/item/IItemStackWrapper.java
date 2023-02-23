package net.perpetualeve.deeppouches.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class IItemStackWrapper implements Container {
	
	IItemHandlerModifiable handler;
	
	public IItemStackWrapper(IItemHandlerModifiable handler) {
		this.handler = handler;
	}
	
	@Override
	public void clearContent() {
		for(int i = 0; i<handler.getSlots();i++) {
			handler.setStackInSlot(i, handler.getStackInSlot(i));
		}
	}

	@Override
	public int getContainerSize() {
		return handler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i<handler.getSlots();i++) {
			if(!handler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int p_18941_) {
		return handler.getStackInSlot(p_18941_);
	}

	@Override
	public ItemStack removeItem(int p_18942_, int p_18943_) {
		return handler.extractItem(p_18942_, p_18943_, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_18951_) {
		ItemStack stack = handler.getStackInSlot(p_18951_);
		handler.setStackInSlot(p_18951_, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int p_18944_, ItemStack p_18945_) {
		handler.setStackInSlot(p_18944_, p_18945_);
	}

	@Override
	public void setChanged() {
		
	}

	@Override
	public boolean stillValid(Player p_18946_) {
		return p_18946_.isAlive();
	}

}
