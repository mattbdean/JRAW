package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Indicates this Thing can be posted by a redditor of elevated privilege
 */
public interface Distinguishable {
	/** The privilege of the poster of this Thing */
	default DistinguishedState getDistinguishedState() {
		String distinguished = getDataNode().get("distinguished").getTextValue();

		if (distinguished == null) {
			return DistinguishedState.NORMAL;
		}

		return DistinguishedState.getByJsonValue(distinguished);
	}

	public JsonNode getDataNode();
}
