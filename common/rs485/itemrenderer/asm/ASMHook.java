package rs485.itemrenderer.asm;

import rs485.itemrenderer.RenderClass;

public class ASMHook {
	public static void mainMenuButtonHook() {
		RenderClass.textureWidth = 512;
		RenderClass.textureHeight = 512;
		RenderClass.imagePath = "G:/renders";
		new RenderClass();
	}
}
