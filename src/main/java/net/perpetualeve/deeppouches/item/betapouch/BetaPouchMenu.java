package net.perpetualeve.deeppouches.item.betapouch;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.perpetualeve.deeppouches.DeepPouches;

public class BetaPouchMenu extends AbstractContainerMenu {
	private final Container container;
	public int count;
	public int offSetCount;

	public BetaPouchMenu(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(DeepPouches.beta_slots));
	}

	public BetaPouchMenu(int id, Inventory inv, Container container) {
		super(DeepPouches.BETA_POUCH_MENU, id);
		this.container = container;
		container.startOpen(inv.player);
		
		int o = count = DeepPouches.beta_slots;
		int q = (int) (54-o)/9;
		for(int i=0; i< Math.min(o,54); ++i) {
			int y = i/9;
			int x = i % 9;
			
			if(i==0) offSetCount = (y*18+q*18);
			this.addSlot(new BetaPouchSlot(container, i, 8 + x*18, (y*18+q*18)-12));
		}

		 for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 9; ++j) {
	            this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 112 + i * 18));
	         }
	      }

	      for(int k = 0; k < 9; ++k) {
	         this.addSlot(new Slot(inv, k, 8 + k * 18, 170));
	      }
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	public void removed(Player p_40197_) {
		super.removed(p_40197_);
		this.container.stopOpen(p_40197_);
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_38942_);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (p_38942_ < this.container.getContainerSize()) {
				if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}
}
