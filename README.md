#Item Renderer

Item Renderer is a small Minecraft modification that requires Forge. It does create rendered images from all of your Minecraft items and blocks.

Item Renderer  Copyright &copy; 2013  RS485

##Building

If you want to build this on your own, you are free to do that. But be sure you change the variables in rs485.itemrenderer.asm.ASMHook (this is temporary, they will not stay hardcoded)

Normal build commands are `ant setup` and `ant simple-package`. As this is still for Minecraft 1.5.2 (will update soon), you need to put lwjgl 2.9.0 in your mcp folder after `ant setup`.