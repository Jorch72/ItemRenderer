package rs485.itemrenderer;

import rs485.itemrenderer.SrgMapping.MappingType;

public class MissingMappingException extends Exception {
	private static final long serialVersionUID = 2112896795392908320L;

	public MissingMappingException(MappingType type, String packet, String name) {
		super("Missing mapping at " + type.toString() + ": " + packet + " " + name);
	}
}
