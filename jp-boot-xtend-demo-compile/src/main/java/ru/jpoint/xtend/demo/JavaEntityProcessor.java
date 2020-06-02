package ru.jpoint.xtend.demo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.eclipse.xtend.lib.annotations.AccessorsProcessor;
import org.eclipse.xtend.lib.annotations.ToString;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.ValidationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMemberDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend.lib.macro.expression.Expression;
import org.eclipse.xtend.lib.macro.services.AnnotationReferenceBuildContext;
import org.eclipse.xtend2.lib.StringConcatenationClient;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
public class JavaEntityProcessor extends EntityProcessor {
	
	@Override
	public void doValidate(List<? extends ClassDeclaration> annotatedClasses, ValidationContext context) {
		super.doValidate(annotatedClasses, context);
		annotatedClasses.stream()
				.filter(declaration -> declaration.findAnnotation(context.findTypeGlobally(ToString.class)) == null)
				.forEach(declaration -> context.addWarning(declaration, "Ты не добавил аннотацию @ToString"));
	}

	@Override
	public void doValidate(final ClassDeclaration annotatedClass, final ValidationContext context) {
		super.doValidate(annotatedClass, context);
		IterableExtensions.forEach(annotatedClass.getTypeParameters(), typeParam -> {
			typeParam.getSimpleName();
		});
		for (FieldDeclaration field : annotatedClass.getDeclaredFields()) {
			if (hasAnnotation(field, context.findTypeGlobally(ManyToOne.class))
					&& hasAnnotation(field, context.findTypeGlobally(Column.class))) {
				context.addError(field, "@Column(s) not allowed on a @ManyToOne property, use @JoinColumn(s)");
			}
		}
	}

	
	private boolean hasAnnotation(FieldDeclaration field, Type findTypeGlobally) {
		return field.findAnnotation(findTypeGlobally) != null;
	}

	@Override
	public void doRegisterGlobals(final ClassDeclaration annotatedClass, final RegisterGlobalsContext context) {		
		context.registerInterface(annotatedClass.getQualifiedName() + "Repository");
	    context.registerClass(annotatedClass.getQualifiedName() + "Service");
	}
	
	@Override
	public void doTransform(MutableClassDeclaration entity, TransformationContext context) {
		super.doTransform(entity, context);
				
		entity.addAnnotation(context.newAnnotationReference(Entity.class));
		
		createRepository(entity, context);	
		createService(entity, context);
	}
	
	private void createRepository(final MutableClassDeclaration entity, final TransformationContext context) {
		final MutableInterfaceDeclaration repositoryType = context.findInterface(entity.getQualifiedName() + "Repository");
		context.setPrimarySourceElement(repositoryType, entity);


		final TypeReference entityType = context.newSelfTypeReference(entity);
		final TypeReference keyType = context.newTypeReference(Serializable.class);

		TypeReference repositoryInterfaceTypeReference = context.newTypeReference(
				PagingAndSortingRepository.class,
				entityType, 
				keyType);

		repositoryType.setExtendedInterfaces(Collections.unmodifiableList(Arrays.asList(repositoryInterfaceTypeReference)));

		IterableExtensions.filter(entity.getDeclaredFields(), field -> {
			return !field.isTransient() && !"id".equals(field.getSimpleName());
		}).forEach(field -> {
			repositoryType.addMethod("findПожалуйстаBy" + StringExtensions.toFirstUpper(field.getSimpleName()), method -> {
				method.setReturnType(isUnique(field, context) ? 
								entityType : 
								context.newTypeReference(List.class, entityType));
				method.addParameter(field.getSimpleName(), field.getType())
						.addAnnotation(context.newAnnotationReference(Param.class, annotation -> {
							annotation.set("value", field.getSimpleName());
						}));
			});
		});
	}

	private boolean isUnique(MutableFieldDeclaration field, TransformationContext context) {
		AnnotationReference columnAnnotation = field.findAnnotation(context.findTypeGlobally(Column.class));
		return columnAnnotation != null && columnAnnotation.getBooleanValue("unique");
	}
	
	private void createService(final MutableClassDeclaration entity, final TransformationContext context) {
		final MutableClassDeclaration serviceType = context.findClass(entity.getQualifiedName() + "Service");
		final MutableInterfaceDeclaration interfaceType = context.findInterface(entity.getQualifiedName() + "Repository");
		final TypeReference entityType = context.newSelfTypeReference(entity);

		serviceType.addAnnotation(context.newAnnotationReference(Service.class));

		serviceType.addField("repository", field -> {
			field.setType(context.newSelfTypeReference(interfaceType));
			field.addAnnotation(context.newAnnotationReference(Autowired.class));
		});

		serviceType.addMethod("findById", method -> {
			method.addParameter("id", context.newTypeReference(Serializable.class));
			method.addAnnotation(context.newAnnotationReference(Transactional.class, tr -> {
				tr.setBooleanValue("readOnly", true);
			}));
			method.setReturnType(entityType);
			createFindByIdMethodBody(method);
		});
	}
}
