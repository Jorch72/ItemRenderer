package rs485.itemrenderer;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "ItemRenderer", /* %------------CERTIFICATE-SUM-----------% */ version = "%VERSION%", useMetadata = true)
public class ItemRenderer {
	@Instance("ItemRenderer")
	private static ItemRenderer instance;

	private org.apache.logging.log4j.Logger modLogger;

	public static ItemRenderer getInstance() {
		return instance;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modLogger = event.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		// init
	}

	public Logger getModLogger() {
		return this.modLogger;
	}
}
