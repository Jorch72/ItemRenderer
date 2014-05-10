package rs485.itemrenderer.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.lwjgl.opengl.GLContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import rs485.itemrenderer.MissingMappingException;
import rs485.itemrenderer.SrgMapping;
import rs485.itemrenderer.SrgMapping.MappingType;

import java.util.ListIterator;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class MainMenuTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.endsWith("net.minecraft.client.gui.GuiMainMenu")) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			for (MethodNode mv : node.methods) {
				String methodName = mv.name;
				try {
					methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, mv.name, mv.desc);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String translatedName = methodName;
				if (SrgMapping.getInstance().isTranslationAvailableToNormal(MappingType.METHOD, transformedName, methodName)) {
					try {
						translatedName = SrgMapping.getInstance().translateToNormal(MappingType.METHOD, transformedName, methodName)[1];
					} catch (MissingMappingException e) {
					}
				}
				if ("initGui".equals(translatedName)) {
					MethodNode newMethod = new MethodNode(mv.access, mv.name, mv.desc, mv.signature, mv.exceptions.toArray(new String[]{})) {
						@Override
						public void visitLineNumber(int line, Label start) {
							super.visitLineNumber(line, start);
							if (line == 201) {
								visitFrame(F_SAME, 0, null, 0, null);
								visitVarInsn(ALOAD, 0);
								visitTypeInsn(NEW, "net/minecraft/client/gui/GuiButton");
								visitInsn(DUP);
								visitIntInsn(BIPUSH, 6);
								visitVarInsn(ALOAD, 0);
								try {
									visitFieldInsn(GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "width")[1],
											"I");
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitInsn(ICONST_2);
								visitInsn(IDIV);
								visitIntInsn(BIPUSH, 100);
								visitInsn(ISUB);
								visitVarInsn(ILOAD, 3);
								visitIntInsn(BIPUSH, 48);
								visitInsn(IADD);
								visitIntInsn(BIPUSH, 98);
								visitIntInsn(BIPUSH, 20);
								visitLdcInsn("Mods");
								visitMethodInsn(INVOKESPECIAL, "net/minecraft/client/gui/GuiButton", "<init>", "(IIIIILjava/lang/String;)V");
								visitFieldInsn(PUTFIELD, "net/minecraft/client/gui/GuiMainMenu", "fmlModButton",
										"Lnet/minecraft/client/gui/GuiButton;");
								visitLabel(new Label());
								// visitLineNumber(202, ...);
								visitTypeInsn(NEW, "net/minecraft/client/gui/GuiButton");
								visitInsn(DUP);
								visitIntInsn(SIPUSH, 1001);
								visitVarInsn(ALOAD, 0);
								try {
									visitFieldInsn(GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "width")[1],
											"I");
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitInsn(ICONST_2);
								visitInsn(IDIV);
								visitInsn(ICONST_2);
								visitInsn(IADD);
								visitVarInsn(ILOAD, 3);
								visitIntInsn(BIPUSH, 48);
								visitInsn(IADD);
								visitIntInsn(BIPUSH, 98);
								visitIntInsn(BIPUSH, 20);
								visitLdcInsn("Make Images");
								visitMethodInsn(INVOKESPECIAL, "net/minecraft/client/gui/GuiButton", "<init>", "(IIIIILjava/lang/String;)V");
								visitVarInsn(ASTORE, 4);
								if (!GLContext.getCapabilities().OpenGL43) {
									visitLabel(new Label());
									//visitLineNumber(203, ...);
									visitVarInsn(ALOAD, 4);
									visitInsn(ICONST_0);
									try {
										visitFieldInsn(PUTFIELD, "net/minecraft/client/gui/GuiButton",
												SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiButton", "enabled")[1],
												"Z");
									} catch (MissingMappingException e) {
										e.printStackTrace();
									}
								}
								visitLabel(new Label());
								//visitLineNumber(204, ...);
								visitFrame(F_APPEND, 1, new Object[]{"net/minecraft/client/gui/GuiButton"}, 0, null);
								visitVarInsn(ALOAD, 0);
								try {
									visitFieldInsn(GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance()
													.translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "buttonList")[1],
											"Ljava/util/List;"
									);
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitVarInsn(ALOAD, 4);
								visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
								visitInsn(POP);
							}
						}
					};

					boolean skip = false;
					ListIterator<AbstractInsnNode> i = mv.instructions.iterator();
					while (i.hasNext()) {
						AbstractInsnNode o = i.next();
						if (o instanceof LabelNode) {
							if (skip) {
								skip = false;
							}
						}
						if (skip) {
							i.remove();
						}
						if (o instanceof LineNumberNode) {
							if (((LineNumberNode) o).line == 201) {
								skip = true;
							}
						}
					}

					mv.accept(newMethod);
					node.methods.set(node.methods.indexOf(mv), newMethod);
				} else if ("actionPerformed".equals(translatedName)) {
					MethodNode newMethod = new MethodNode(mv.access, mv.name, mv.desc, mv.signature, mv.exceptions.toArray(new String[]{})) {
						@Override
						public void visitCode() {
							visitLabel(new Label());
							visitVarInsn(ALOAD, 1);
							try {
								visitFieldInsn(GETFIELD, "net/minecraft/client/gui/GuiButton",
										SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiButton", "id")[1], "I");
							} catch (MissingMappingException e) {
								e.printStackTrace();
							}
							visitIntInsn(SIPUSH, 1001);
							Label l1 = new Label();
							visitJumpInsn(IF_ICMPNE, l1);
							visitLabel(new Label());
							visitMethodInsn(INVOKESTATIC, "rs485/itemrenderer/asm/ASMHook", "mainMenuButtonHook", "()V");
							visitLabel(l1);
						}
					};

					mv.accept(newMethod);
					node.methods.set(node.methods.indexOf(mv), newMethod);
				}
			}
			ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		}
		return bytes;
	}

}
