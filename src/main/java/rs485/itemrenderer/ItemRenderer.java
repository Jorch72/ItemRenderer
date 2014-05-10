package rs485.itemrenderer;

import java.util.logging.Logger;

import lombok.Getter;
import net.minecraft.crash.CallableMinecraftVersion;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ItemRenderer", /* %------------CERTIFICATE-SUM-----------% */ version = "%VERSION%", useMetadata = true)
public class ItemRenderer {
	@Getter
	@Instance("ItemRenderer")
	private static ItemRenderer instance;

	@Getter
	private Logger log;

	public String getMCVersion() {
		return new CallableMinecraftVersion(null).minecraftVersion();
	}

	@PreInit
	public void preInitialization(FMLPreInitializationEvent evt)
			throws NoSuchFieldException, SecurityException {
		log = evt.getModLog();
	}

	@Init
	public void initialization(FMLInitializationEvent event) {
		// init
	}
}
