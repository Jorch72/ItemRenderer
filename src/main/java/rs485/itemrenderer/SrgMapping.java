package rs485.itemrenderer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.Mod.Instance;
import rs485.itemrenderer.asm.ClassTransformerLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

public class SrgMapping {
	public static SrgMapping getInstance() {
		return SrgMapping.instance;
	}

	public static enum MappingType {
		METHOD,
		FIELD,
	}

	@Instance("SrgMapping")
	private static SrgMapping instance;

	private BiMap<String, String> mappings;

	public SrgMapping() {
		instance = this;

		Properties mappingProps = new Properties();
		InputStream in = null;
		try {
			in = ItemRenderer.class.getResourceAsStream("/mapping.properties");
			mappingProps.load(in);
		} catch (IOException e) {
			System.out.println("Error: Could not load mapping resource.");
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

		mappings = HashBiMap.create();
		for (Entry<Object, Object> e : mappingProps.entrySet()) {
			mappings.put((String) e.getKey(), (String) e.getValue());
		}
	}

	public boolean isTranslationAvailableToSrg(MappingType type, String packet, String name) {
		if (ClassTransformerLoader.runtimeDeobfuscationEnabled) {
			String full = null;

			switch (type) {
				case FIELD:
					full = packet.replaceAll("\\.", "/") + "/" + name;
					break;
				case METHOD:
					full = packet.replaceAll("/", ".") + "." + name;
					break;
			}

			return mappings.containsKey(full);
		}
		return false;
	}

	public String[] translateToSrg(MappingType type, String packet, String name) throws MissingMappingException {
		if (ClassTransformerLoader.runtimeDeobfuscationEnabled) {
			String full = null;

			switch (type) {
				case FIELD:
					full = packet.replaceAll("\\.", "/") + "/" + name;
					break;
				case METHOD:
					full = packet.replaceAll("/", ".") + "." + name;
					break;
			}
			if (mappings.containsKey(full)) {
				String fullMap = mappings.get(full);
				String[] str = fullMap.split("/");
				String func = str[str.length - 1];
				return new String[]{fullMap.substring(0, fullMap.length() - func.length()), func};
			} else {
				throw new MissingMappingException(type, packet, name);
			}
		}
		return new String[]{packet, name};
	}

	public boolean isTranslationAvailableToNormal(MappingType type, String packet, String srgName) {
		if (ClassTransformerLoader.runtimeDeobfuscationEnabled) {
			String full = packet.replaceAll("\\.", "/") + "/" + srgName;

			return mappings.inverse().containsKey(full);
		}
		return false;
	}

	public String[] translateToNormal(MappingType type, String packet, String srgName) throws MissingMappingException {
		if (ClassTransformerLoader.runtimeDeobfuscationEnabled) {
			BiMap<String, String> inverseMappings = mappings.inverse();
			String full = packet.replaceAll("\\.", "/") + "/" + srgName;
			if (inverseMappings.containsKey(full)) {
				String fullMap = inverseMappings.get(full);
				String[] str = null;
				switch (type) {
					case FIELD:
						str = fullMap.split("/");
						break;
					case METHOD:
						str = fullMap.split("\\.");
						break;
				}

				String func = str[str.length - 1];
				return new String[]{fullMap.substring(0, fullMap.length() - func.length()), func};
			} else {
				throw new MissingMappingException(type, packet, srgName);
			}
		}
		return new String[]{packet, srgName};
	}
}
