package no.norgesgruppen.demo;

import java.util.ArrayList;
import java.util.List;

public class CacheableContext {
	String packageName;
	String className;
	String inheritedClassName;
	List<CacheableMethod> cacheables = new ArrayList<CacheableMethod>();

	public CacheableContext(String className, String inheritedClassName, String packageName) {
		this.className = className;
		this.inheritedClassName = inheritedClassName;
		this.packageName = packageName;
	}

	static class CacheableMethod {
		String keyClass;
		String returnType;
		String visibility = "public";
		String cache;
		String arguments;
		String typeArgs;
		String signature;
		String method;
	    List<Field> fields = new ArrayList<Field>();
	    List<Field> primitives = new ArrayList<Field>();
        List<Field> allFields = new ArrayList<Field>();
        String packageName;
    }

    static class Field {
        String name;
        String type;

        Field(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
