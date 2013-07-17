package rs485.itemrenderer.asm;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import rs485.itemrenderer.MissingMappingException;
import rs485.itemrenderer.SrgMapping;
import rs485.itemrenderer.SrgMapping.MappingType;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.IClassTransformer;

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
					MethodNode newMethod = new MethodNode(mv.access, mv.name, mv.desc, mv.signature, mv.exceptions.toArray(new String[] {})) {
						@Override
						public void visitLineNumber(int line, Label start) {
							super.visitLineNumber(line, start);
							if (line == 201) {
								visitFrame(Opcodes.F_SAME, 0, null, 0, null);
								visitVarInsn(Opcodes.ALOAD, 0);
								visitTypeInsn(Opcodes.NEW, "net/minecraft/client/gui/GuiButton");
								visitInsn(Opcodes.DUP);
								visitIntInsn(Opcodes.BIPUSH, 6);
								visitVarInsn(Opcodes.ALOAD, 0);
								try {
									visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "width")[1],
											"I");
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitInsn(Opcodes.ICONST_2);
								visitInsn(Opcodes.IDIV);
								visitIntInsn(Opcodes.BIPUSH, 100);
								visitInsn(Opcodes.ISUB);
								visitVarInsn(Opcodes.ILOAD, 3);
								visitIntInsn(Opcodes.BIPUSH, 48);
								visitInsn(Opcodes.IADD);
								visitIntInsn(Opcodes.BIPUSH, 98);
								visitIntInsn(Opcodes.BIPUSH, 20);
								visitLdcInsn("Mods");
								visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/client/gui/GuiButton", "<init>", "(IIIIILjava/lang/String;)V");
								visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/gui/GuiMainMenu", "fmlModButton",
										"Lnet/minecraft/client/gui/GuiButton;");
								visitLabel(new Label());
								// visitLineNumber(202, l28);
								visitVarInsn(Opcodes.ALOAD, 0);
								try {
									visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance()
													.translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "buttonList")[1],
											"Ljava/util/List;");
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitTypeInsn(Opcodes.NEW, "net/minecraft/client/gui/GuiButton");
								visitInsn(Opcodes.DUP);
								visitIntInsn(Opcodes.SIPUSH, 1001);
								visitVarInsn(Opcodes.ALOAD, 0);
								try {
									visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu",
											SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiMainMenu", "width")[1],
											"I");
								} catch (MissingMappingException e) {
									e.printStackTrace();
								}
								visitInsn(Opcodes.ICONST_2);
								visitInsn(Opcodes.IDIV);
								visitInsn(Opcodes.ICONST_2);
								visitInsn(Opcodes.IADD);
								visitVarInsn(Opcodes.ILOAD, 3);
								visitIntInsn(Opcodes.BIPUSH, 48);
								visitInsn(Opcodes.IADD);
								visitIntInsn(Opcodes.BIPUSH, 98);
								visitIntInsn(Opcodes.BIPUSH, 20);
								visitLdcInsn("Make Images");
								visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/client/gui/GuiButton", "<init>", "(IIIIILjava/lang/String;)V");
								visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
								visitInsn(Opcodes.POP);
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
					MethodNode newMethod = new MethodNode(mv.access, mv.name, mv.desc, mv.signature, mv.exceptions.toArray(new String[] {})) {
						@Override
						public void visitCode() {
							visitLabel(new Label());
							visitVarInsn(Opcodes.ALOAD, 1);
							try {
								visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiButton",
										SrgMapping.getInstance().translateToSrg(MappingType.FIELD, "net/minecraft/client/gui/GuiButton", "id")[1], "I");
							} catch (MissingMappingException e) {
								e.printStackTrace();
							}
							visitIntInsn(Opcodes.SIPUSH, 1001);
							Label l1 = new Label();
							visitJumpInsn(Opcodes.IF_ICMPNE, l1);
							visitLabel(new Label());
							visitMethodInsn(Opcodes.INVOKESTATIC, "rs485/itemrenderer/asm/ASMHook", "mainMenuButtonHook", "()V");
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
