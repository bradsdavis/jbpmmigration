package org.jbpm.migration.xml;

import org.apache.commons.lang3.StringUtils;

public class IdGeneratorUtil {

	public static String translate(String name) {
		name = name.replaceAll("[^a-zA-Z0-9]", "");
		name = StringUtils.replace(name, " ", "");
		return name;
	}
}
