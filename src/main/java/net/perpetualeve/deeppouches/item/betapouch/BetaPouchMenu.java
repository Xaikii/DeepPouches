package net.perpetualeve.deeppouches.item.betapouch;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.perpetualeve.deeppouches.DeepPouches;
import net.perpetualeve.deeppouches.item.SimplePouchContainer;

public class BetaPouchMenu extends AbstractContainerMenu {
	private final Container container;
	public int count;
	public int offSetCount;

	public BetaPouchMenu(int id, Inventory inv) {
		this(id, inv, new SimplePouchContainer(DeepPouches.beta_slots));
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
	
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
	      boolean flag = false;
	      int i = p_38905_;
	      if (p_38907_) {
	         i = p_38906_ - 1;
	      }

	      if (p_38904_.isStackable()) {
	         while(!p_38904_.isEmpty()) {
	            if (p_38907_) {
	               if (i < p_38905_) {
	                  break;
	               }
	            } else if (i >= p_38906_) {
	               break;
	            }

	            Slot slot = this.slots.get(i);
	            ItemStack itemstack = slot.getItem();
	            if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
	               int j = itemstack.getCount() + p_38904_.getCount();
	               int maxSize = slot.getMaxStackSize();
	               if (j <= maxSize) {
	                  p_38904_.setCount(0);
	                  itemstack.setCount(j);
	                  slot.setChanged();
	                  flag = true;
	               } else if (itemstack.getCount() < maxSize) {
	                  p_38904_.shrink(maxSize - itemstack.getCount());
	                  itemstack.setCount(maxSize);
	                  slot.setChanged();
	                  flag = true;
	               }
	            }

	            if (p_38907_) {
	               --i;
	            } else {
	               ++i;
	            }
	         }
	      }

	      if (!p_38904_.isEmpty()) {
	         if (p_38907_) {
	            i = p_38906_ - 1;
	         } else {
	            i = p_38905_;
	         }

	         while(true) {
	            if (p_38907_) {
	               if (i < p_38905_) {
	                  break;
	               }
	            } else if (i >= p_38906_) {
	               break;
	            }

	            Slot slot1 = this.slots.get(i);
	            ItemStack itemstack1 = slot1.getItem();
	            if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
	               if (p_38904_.getCount() > slot1.getMaxStackSize()) {
	                  slot1.set(p_38904_.split(slot1.getMaxStackSize()));
	               } else {
	                  slot1.set(p_38904_.split(p_38904_.getCount()));
	               }

	               slot1.setChanged();
	               flag = true;
	               break;
	            }

	            if (p_38907_) {
	               --i;
	            } else {
	               ++i;
	            }
	         }
	      }

	      return flag;
	   }
}
