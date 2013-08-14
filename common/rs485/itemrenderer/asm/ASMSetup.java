package rs485.itemrenderer.asm;

import java.util.Map;

import lombok.Getter;

import org.lwjgl.opengl.GL43;

import rs485.itemrenderer.SrgMapping;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class ASMSetup implements IFMLCallHook {
	@Getter
	private static boolean opengl43 = false;

	@Override
	public Void call() {
		try {
			GL43.class.getName();
			opengl43 = true;
		} catch (Exception e) {
		}

		new SrgMapping();
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}
}
