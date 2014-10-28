package no.norgesgruppen.demo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import no.norgesgruppen.demo.annotations.Getter;
import no.norgesgruppen.demo.annotations.Setter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.Set;

import static java.util.Locale.ENGLISH;
import static no.norgesgruppen.demo.ValueObjectContext.GetterSetterSignature;

@SupportedAnnotationTypes(
		value = {
				"no.norgesgruppen.demo.annotations.Getter",
				"no.norgesgruppen.demo.annotations.Setter"
		})
@SupportedSourceVersion(value = SourceVersion.RELEASE_6)
public class GetterSetterProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver() || annotations.size() == 0) {
			return false;
		}
		processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing started");

		try {
			Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Getter.class);

			TypeElement classElement = null;
			PackageElement packageElement;

			ValueObjectContext valueObjectContext = null;
			for (Element element : elementsAnnotatedWith) {
				classElement = (TypeElement) element.getEnclosingElement();
				packageElement = (PackageElement) classElement.getEnclosingElement();
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing: " + classElement);

				if (valueObjectContext == null) {
					valueObjectContext = new ValueObjectContext(
							classElement.getSimpleName().toString().replace("Spec", ""),
							classElement.getSimpleName().toString(),
							packageElement.getQualifiedName().toString()
					);

				}

				if (element.getKind() == ElementKind.FIELD) {
					VariableElement field = (VariableElement) element;

					GetterSetterSignature getter = null;
					Getter getterAnnotation = element.getAnnotation(Getter.class);
					if (getterAnnotation != null) {
						getter = new GetterSetterSignature(field.asType().toString(),
								field.getSimpleName().toString(),
								capitalize(field.getSimpleName().toString()),
								"public");
					}

					GetterSetterSignature setter = null;
					Setter setterAnnotation = element.getAnnotation(Setter.class);
					if (setterAnnotation != null) {
						setter = new GetterSetterSignature(
								field.asType().toString(),
								field.getSimpleName().toString(),
								capitalize(field.getSimpleName().toString()),
								"public");
					}

					if (getter != null || setter != null) {
						valueObjectContext.gettersetters.add(new ValueObjectContext.GetterSetter(getter, setter));
					}
				}
			}

			JavaFileObject fooTest = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName().toString().replace("Spec", ""));
			OutputStream outputStream = fooTest.openOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(outputStream, "UTF-8");

			DefaultMustacheFactory mf = new DefaultMustacheFactory("templates/");
			Mustache mustache = mf.compile("ValueObject.mustache");
			mustache.execute(out, valueObjectContext).flush();

			out.close();
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "HElloe");
			e.printStackTrace();
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
