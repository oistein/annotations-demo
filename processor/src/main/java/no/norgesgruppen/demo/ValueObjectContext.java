package no.norgesgruppen.demo;

import java.util.ArrayList;
import java.util.List;

public class ValueObjectContext {
	String className;
	String inheritedClassName;
	String packageName;
	List<GetterSetter> gettersetters = new ArrayList<GetterSetter>();

	public ValueObjectContext(String className, String inheritedClassName, String packageName) {
		this.className = className;
		this.inheritedClassName = inheritedClassName;
		this.packageName = packageName;
	}

	static class GetterSetter {
		GetterSetterSignature getter, setter;

		GetterSetter(GetterSetterSignature getter, GetterSetterSignature setter) {
			this.getter = getter;
			this.setter = setter;
		}
	}

	static class GetterSetterSignature {
		String type, name, visibility, nameCamel;

		GetterSetterSignature(String type, String name, String nameCamel, String visibility) {
			this.type = type;
			this.name = name;
			this.nameCamel = nameCamel;
			this.visibility = visibility;
		}
	}
}
