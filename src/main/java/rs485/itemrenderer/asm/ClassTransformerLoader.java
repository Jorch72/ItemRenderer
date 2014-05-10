package rs485.itemrenderer.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.util.Map;

@TransformerExclusions({"rs485.itemrenderer.asm"})
public class ClassTransformerLoader implements IFMLLoadingPlugin {
	public static boolean runtimeDeobfuscationEnabled = false;

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"rs485.itemrenderer.asm.MainMenuTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return "rs485.itemrenderer.asm.ASMSetup";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobfuscationEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
