#Item Renderer

Item Renderer is a small Minecraft modification that requires Forge. It does create rendered images from all of your Minecraft items and blocks.

Item Renderer  Copyright &copy; 2013  RS485

##Building

If you want to build this on your own, you are free to do that. But be sure you change the variables in rs485.itemrenderer.asm.ASMHook (this is temporary, they will not stay hardcoded)

Normal build commands are `ant setup` and `ant simple-package`. As this is still for Minecraft 1.5.2 (will update soon), you need to put lwjgl 2.9.0 in your mcp folder after `ant setup`.

##Running

To run this, you need Minecraft Forge, lwjgl 2.9.0 and a build (see Building) and put that build in your coremods folder. Now start up your Minecraft and do not maximize it, leave it in a small window. If "Make Images" is greyed out, you do not have lwjgl 2.9.0. If you press on "Make Images" you may see some changes to the screen itself, if your game freezes (the first time you clicked on it) and you do not get anything in the console, you need to kill your game and start it again. That will hopefully be fixed. After you have clicked once and your screen adjusted (and you did not crash) you have to click a second time on that button. This time you should see quite a lot spam in the console, where it says what it is rendering right now. If there are any errors during that progress please report them. After it has finished, your Minecraft should be unfrozen and the console should tell you how long the rendering has taken. Then you might want to look into your output folder you specified in your ASMHook.java. All the images are copyrighted by their respective owners.

###Caveats

* Chests do not render correctly
* Some mod items do render similar to chests
* Thaumcraft breaks the rendering somewhere and you will only get black render images, when rendering items
* If you have too many mods there may be some problems after a while rendering
