package rs485.itemrenderer.asm;

import cpw.mods.fml.relauncher.IFMLCallHook;
import rs485.itemrenderer.SrgMapping;

import java.util.Map;

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
