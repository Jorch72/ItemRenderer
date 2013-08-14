package rs485.itemrenderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import com.google.common.io.Files;

import rs485.itemrenderer.asm.ASMSetup;

public class RenderClass {
	public static String imagePath;
	public static int textureWidth;
	public static int textureHeight;

	private static final String[] blockedItemsList = new String[] {};

	private static final int BYTES_PER_PIXEL = 4;
	private static final boolean isFramebufferEnabled = true;

	private static int mcCanvasWidth;
	private static int mcCanvasHeight;

	private int fbObject;
	private int fbRenderbuffer;
	private int fbColorTex;

	public RenderClass() {
		if (ASMSetup.isOpengl43()) {
			Minecraft mc = Minecraft.getMinecraft();

			mcCanvasWidth = mc.mcCanvas.getWidth();
			mcCanvasHeight = mc.mcCanvas.getHeight();
			int sWidth = Math.max(1024, textureWidth);
			int sHeight = textureHeight;

			if (mcCanvasWidth != sWidth && mcCanvasHeight != sHeight) {
				mc.mcCanvas.setSize(sWidth, sHeight);
			} else {
				File imagesFolder = new File(imagePath);
				if (imagesFolder.exists()) {
					for (File file : imagesFolder.listFiles())
						file.delete();
				}
				imagesFolder.mkdirs();
				if (!imagesFolder.exists()) {
					System.out.println("There were problems creating the folders to " + imagePath);
				}

				// Check if GL is error clean
				checkGL();

				if (isFramebufferEnabled) {
					// Setup function for the frame buffer object
					framebufferSetup();
				}

				oglSetup();

				RenderItem itemRenderer = new RenderItem();

				long start = System.currentTimeMillis();

				ItemStack itemstack = null;

				try {
					for (int itemID = 0; itemID < Item.itemsList.length; itemID++) {
						Item item = Item.itemsList[itemID];
						if (item == null) {
							continue;
						}

						System.out.println("Item " + itemID);

						ArrayList<ItemStack> sublist = new ArrayList<ItemStack>();
						item.getSubItems(itemID, null, sublist);
						for (ItemStack damagedItemstack : sublist) {
							System.out.println("  Subitem [" + damagedItemstack.getItemDamage() + "]: " + getUnifiedItemName(damagedItemstack));

							try {
								if (renderItemSafe(mc, itemRenderer, damagedItemstack)) {
									writeImage(imagesFolder, getUnifiedItemName(damagedItemstack));
								} else {
									System.out.println("    Skipped...");
								}
							} catch (OpenGLException e) {
								if (e.getMessage().equals("Stack overflow (1283)")) {
									if (isFramebufferEnabled) {
										framebufferClean();
										framebufferSetup();
									}
									oglSetup();
									System.out.println("Re-setup on stack overflow");
									if (renderItemSafe(mc, itemRenderer, damagedItemstack)) {
										writeImage(imagesFolder, getUnifiedItemName(damagedItemstack));
									} else {
										System.out.println("    Skipped...");
									}
								} else {
									throw e;
								}
							}

							clearToTransparency();
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}

				System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to render");

				// glPopMatrix();
				checkGL();

				RenderHelper.disableStandardItemLighting();

				if (isFramebufferEnabled) {
					framebufferClean();
				}
				mc.mcCanvas.setSize(mcCanvasWidth, mcCanvasHeight);
			}
		}
	}

	private void oglSetup() {
		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glViewport(0, 0, textureWidth, textureHeight);
		checkGL();

		// Clear
		clearToTransparency();

		glDisable(GL11.GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		checkGL();

		glPopAttrib();
		checkGL();

		// glPushMatrix();
		// glMatrixMode(GL_PROJECTION);
		// glLoadIdentity();
		// glOrtho(0.0, textureWidth, 0.0, textureHeight, 0.0, 0.0);
		// glMatrixMode(GL_MODELVIEW);
		// glLoadIdentity();
		float scale = ((float) Math.max(textureWidth, textureHeight)) / 32.0F;
		glScalef(scale, scale, scale);
		// glTranslatef(0.0F, -1.0F, 0.0F);
		checkGL();

		RenderHelper.enableGUIStandardItemLighting();
		glEnable(GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.0F, 0.0F);
		checkGL();
	}

	private String getUnifiedItemName(ItemStack itemstack) {
		String displayName = itemstack.getDisplayName();
		if (displayName.isEmpty()) {
			displayName = itemstack.getItemName().replaceAll("^tile\\.", "");
		}
		if (displayName.isEmpty()) {
			displayName = itemstack.itemID + ":" + itemstack.getItemDamage();
		}
		return "Item " + displayName;
	}

	private void clearToTransparency() {
		glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		checkGL();
	}

	private void framebufferSetup() {
		/*
		// Create texture
		int texID = createEmptyTexture(data);
		checkGL();

		// Create framebuffer and bind the framebuffer
		int fbo = createFramebuffer();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		checkGL();

		// Create renderbuffer and bind the renderbuffer
		int rbo = createRenderbuffer();
		checkGL();

		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
		*/

		// RGBA8 2D texture, D24S8 depth/stencil texture, 256x256
		fbColorTex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbColorTex);
		checkGL();

		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		checkGL();
		// NULL means reserve texture memory, but texels are undefined
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureWidth, textureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		checkGL();
		// You must reserve memory for other mipmaps levels as well either by making a series of calls to
		// glTexImage2D or use glGenerateMipmapEXT(GL11.GL_TEXTURE_2D).
		// Here, we'll use :
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		checkGL();
		// -------------------------
		fbObject = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL_FRAMEBUFFER, fbObject);
		GL43.glFramebufferParameteri(GL_FRAMEBUFFER, GL43.GL_FRAMEBUFFER_DEFAULT_WIDTH, textureWidth);
		GL43.glFramebufferParameteri(GL_FRAMEBUFFER, GL43.GL_FRAMEBUFFER_DEFAULT_HEIGHT, textureHeight);
		// Attach 2D texture to this FBO
		GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, fbColorTex, 0);
		checkGL();
		// -------------------------
		fbRenderbuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, fbRenderbuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, textureWidth, textureHeight);
		checkGL();
		// -------------------------
		// Attach depth buffer to FBO
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, fbRenderbuffer);
		// Also attach as a stencil
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, fbRenderbuffer);
		checkGL();
		// -------------------------
		// Does the GPU support current FBO configuration?
		final int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer incomplete: status " + status);
		}
		// -------------------------
		// and now you can render to GL_TEXTURE_2D
		GL30.glBindFramebuffer(GL_FRAMEBUFFER, fbObject);
		checkGL();
	}

	private void framebufferClean() {
		// Delete resources
		glDeleteTextures(fbColorTex);
		GL30.glDeleteRenderbuffers(fbRenderbuffer);
		// Bind 0, which means render to back buffer, as a result, fb is unbound
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL30.glDeleteFramebuffers(fbObject);
	}

	private boolean renderItemSafe(Minecraft mc, RenderItem itemRenderer, ItemStack itemstack) {
		boolean success = true;
		if (isItemstackBlocked(itemstack))
			return false;

		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glPushMatrix();
		checkGL();

		try {
			renderItem(mc, itemRenderer, itemstack);
		} catch (NullPointerException e) {
			// Reset Tessellator
			Tessellator.instance = new Tessellator();

			try {
				System.out.println("Could not render item " + getUnifiedItemName(itemstack));
			} catch (Throwable e2) {
			}
		} catch (OpenGLException e) {
			// Reset Tessellator
			Tessellator.instance = new Tessellator();

			System.out.println("Unrenderable item ");
			try {
				System.out.println(itemstack.getItemName());
			} catch (Throwable e2) {
				System.out.println(itemstack.toString());
			}
			throw e;
		} catch (Throwable e) {
			// Reset Tessellator
			Tessellator.instance = new Tessellator();

			try {
				System.err.println("Got a problem rendering " + getUnifiedItemName(itemstack));
			} catch (Throwable e2) {
			}
			e.printStackTrace();
			success = false;
		}

		glPopMatrix();
		glPopAttrib();
		checkGL();
		return success;
	}

	private boolean isItemstackBlocked(ItemStack itemstack) {
		for (int i = 0; i < blockedItemsList.length; i++) {
			if (blockedItemsList[i].equalsIgnoreCase(itemstack.getItemName())) {
				return true;
			}
		}
		return false;
	}

	private void renderItem(Minecraft mc, RenderItem itemRenderer, ItemStack itemstack) {
		itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, 0, 0); // mc.displayHeight-textureHeight
		// With empty string, because we don't want to have any text here
		itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, 0, 0, ""); // mc.displayHeight-textureHeight
		checkGL();
	}

	private void writeImage(File imagesFolder, String filename) {
		// Allocate byte buffer
		ByteBuffer data = BufferUtils.createByteBuffer(textureWidth * textureHeight * BYTES_PER_PIXEL);

		glReadPixels(0, 0, textureWidth, textureHeight, GL_RGBA, GL_UNSIGNED_BYTE, data);
		checkGL();

		filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_");

		File image = new File(imagesFolder, filename + ".png");
		int count = 2;
		while (image.exists()) {
			image = new File(imagesFolder, filename + " (" + count + ")" + ".png");
			count++;
		}

		BufferedImage bufImage = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

		int x = 0;
		int y = 0;
		while (data.hasRemaining()) {
			// Order: r g b a
			int[] pixelData = new int[BYTES_PER_PIXEL];
			for (int i = 0; i < BYTES_PER_PIXEL; i++) {
				pixelData[i] = data.get() & 0xFF;
			}
			bufImage.setRGB(x, textureHeight - (y + 1), new Color(pixelData[0], pixelData[1], pixelData[2], pixelData[3]).getRGB());
			x++;
			if (x == textureWidth) {
				x = 0;
				y++;
			}
		}

		try {
			ImageIO.write(bufImage, "png", image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	static int createRenderbuffer() {
		final int rbo = glGenRenderbuffers();
		checkGL();

		glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
		glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL_DEPTH_COMPONENT | GL_COLOR_ATTACHMENT0, textureWidth, textureHeight);
		checkGL();

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_COMPONENT | GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, rbo);
		checkGL();

		glBindRenderbuffer(GL43.GL_RENDERBUFFER, 0);
		return rbo;
	}

	static int createFramebuffer() {
		final int fbo = glGenFramebuffers();
		checkGL();

		return fbo;
	}

	// Old code
	static int createFramebuffer(final int texID) {
		final int fbo = glGenFramebuffers();
		checkGL();

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texID, 0);

		final int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer incomplete: status " + status);
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		return fbo;
	}

	static int createEmptyTexture(final ByteBuffer data)
	{
		final int t = glGenTextures();
		checkGL();

		glBindTexture(GL11.GL_TEXTURE_2D, t);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA8, textureWidth, textureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		glBindTexture(GL11.GL_TEXTURE_2D, 0);
		checkGL();

		return t;
	}

	// Old code
	static int createEmptyTexture()
	{
		final int b = glGenBuffers();
		checkGL();

		final int size = textureWidth * textureHeight * BYTES_PER_PIXEL;
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, b);
		glBufferData(GL_PIXEL_UNPACK_BUFFER, size, GL_STATIC_DRAW);
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
		checkGL();

		final int t = glGenTextures();
		checkGL();

		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, b);
		glBindTexture(GL11.GL_TEXTURE_2D, t);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA, textureWidth, textureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0L);
		glBindTexture(GL11.GL_TEXTURE_2D, 0);
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
		checkGL();

		return t;
	}
	*/

	static void checkGL()
	{
		final int e = glGetError();
		if (e != GL_NO_ERROR) {
			throw new OpenGLException(e);
		}
	}
}
