package no.norgesgruppen.demo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import no.norgesgruppen.demo.annotations.Cacheable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import static java.util.Locale.ENGLISH;

@SupportedAnnotationTypes({"no.norgesgruppen.demo.annotations.Cacheable"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_6)
public class CacheableProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver() || annotations.size() == 0) {
			return false;
		}
		processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing started");

		try {
			Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Cacheable.class);

			TypeElement classElement = null;
			PackageElement packageElement;

			CacheableContext cacheableContext = null;
			for (Element element : elementsAnnotatedWith) {
				classElement = (TypeElement) element.getEnclosingElement();
				packageElement = (PackageElement) classElement.getEnclosingElement();
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing: " + classElement);

                String packageName = packageElement.getQualifiedName().toString();
				if (cacheableContext == null) {
                    cacheableContext = new CacheableContext(
							classElement.getSimpleName().toString() + "Default",
							classElement.getSimpleName().toString(),
                            packageName
					);
				}

				if (element.getKind() == ElementKind.METHOD) {
					ExecutableElement method = (ExecutableElement) element;
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing method " + method);

					CacheableContext.CacheableMethod cacheableMethod = new CacheableContext.CacheableMethod();
					cacheableMethod.returnType = method.getReturnType().toString();
					cacheableMethod.keyClass = capitalize(method.getSimpleName() + "Key");
					cacheableMethod.cache = method.getSimpleName() + "Cache";
					StringBuilder argumentsBuilder = new StringBuilder();
					StringBuilder typeArgsBuilder = new StringBuilder();
					for (int i = 0; i < method.getParameters().size(); i++) {
                        VariableElement parameter = method.getParameters().get(i);
                        CacheableContext.Field field = new CacheableContext.Field(parameter.getSimpleName().toString(),
                                parameter.asType().toString());
                        cacheableMethod.allFields.add(field);
                        if (parameter.asType().getKind().isPrimitive()) {
                            cacheableMethod.primitives.add(field);
                        } else {
                            cacheableMethod.fields.add(field);
                        }

						if (i == 0) {
							typeArgsBuilder.append(parameter.asType().toString())
										   .append(" ")
										   .append(parameter.getSimpleName());

							argumentsBuilder.append(parameter.getSimpleName());
						} else {
							argumentsBuilder.append(", ").append(parameter.getSimpleName());

							typeArgsBuilder
									.append(", ").append(parameter.asType().toString())
									.append(" ")
									.append(parameter.getSimpleName());

						}
					}
					cacheableMethod.arguments = argumentsBuilder.toString();
					cacheableMethod.typeArgs = typeArgsBuilder.toString();
					cacheableMethod.method = method.getSimpleName().toString();
                    cacheableMethod.packageName = packageName;
					cacheableContext.cacheables.add(cacheableMethod);

                    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                            packageName + "." + cacheableMethod.keyClass);
                    Writer out = jfo.openWriter();

                    DefaultMustacheFactory mf = new DefaultMustacheFactory("templates/");
                    Mustache mustache = mf.compile("CacheableKey.mustache");
                    mustache.execute(out, cacheableMethod).flush();

                    out.close();
				}
			}

			JavaFileObject fooTest = processingEnv.getFiler().createSourceFile(
                    classElement.getQualifiedName().toString() + "Default");
			Writer out = fooTest.openWriter();

			DefaultMustacheFactory mf = new DefaultMustacheFactory("templates/");
			Mustache mustache = mf.compile("Cacheable.mustache");
			mustache.execute(out, cacheableContext).flush();

			out.close();
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}

		return true;
	}

	public String capitalize(String name) {
		return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
	}

	public static void main(String[] args) throws IOException {
		DefaultMustacheFactory mf = new DefaultMustacheFactory("templates/");
		Mustache mustache = mf.compile("ValueObject.mustache");
		mustache.execute(new PrintWriter(System.out), new Object()).flush();
	}
}
