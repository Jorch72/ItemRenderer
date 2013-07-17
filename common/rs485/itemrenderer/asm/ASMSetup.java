package rs485.itemrenderer.asm;

import java.util.Map;

import rs485.itemrenderer.SrgMapping;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class ASMSetup implements IFMLCallHook {
	@Override
	public Void call() {
		new SrgMapping();
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}
}
