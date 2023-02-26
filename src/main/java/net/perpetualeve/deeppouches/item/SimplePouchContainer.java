package net.perpetualeve.deeppouches.item;

import net.minecraft.world.SimpleContainer;

public class SimplePouchContainer extends SimpleContainer {

	public SimplePouchContainer(int p_19150_) {
		super(p_19150_);
	}

	@Override
	public int getMaxStackSize() {
		return Integer.MAX_VALUE;
	}
}
