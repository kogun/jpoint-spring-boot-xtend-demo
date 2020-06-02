package ru.jpoint.xtend.demo

import org.eclipse.xtend.lib.annotations.AccessorsProcessor
import org.eclipse.xtend.lib.macro.AbstractClassProcessor
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration

/* @Target(ElementType.TYPE)
@Active(EntityProcessor)
annotation Entity {
}*/

class EntityProcessor extends AbstractClassProcessor {
	
	override doTransform(MutableClassDeclaration it, extension TransformationContext context) {
		addField("dirty", [
			type = primitiveBoolean
			transient = true
			initializer = '''«false»'''
		])		
		declaredFields.forEach[modifySetter(context)]
	}
	
	def void modifySetter(MutableFieldDeclaration it, extension TransformationContext context) {
			
		extension val util = new AccessorsProcessor.Util(context)
				
		val setter = declaringType.findDeclaredMethod(setterName, type)
		if (setter !== null  && !setter.isThePrimaryGeneratedJavaElement) {			
			setter.returnType = declaringType.newSelfTypeReference
			setter.body = '''
				this.«simpleName» = «simpleName»;
				return this;
			'''
		}
	}

	def protected void createFindByIdMethodBody(MutableMethodDeclaration it) {
		body = '''
			return repository.findOne(id);
		'''
	}







	
	/*def void createRepository(MutableClassDeclaration it, extension TransformationContext context) {
		//repository
		val repositoryType = findInterface(it.qualifiedName + "Repository")
		//interfaceType.extendedInterfaces = #[CrudRepository.newTypeReference(type.newSelfTypeReference, Serializable.newTypeReference)]
		repositoryType.extendedInterfaces = #[PagingAndSortingRepository.newTypeReference(newSelfTypeReference, Serializable.newTypeReference)]
		
		//для всех свойств добавить find методы
		val entityType = it.newSelfTypeReference
		
		declaredFields.filter [
			!static && !transient && !"id".equals(simpleName)
		].forEach[ field |
			repositoryType.addMethod("findBy" + field.simpleName.toFirstUpper, [
				returnType = List.newTypeReference(entityType)
				addParameter(field.simpleName, field.type)
				.addAnnotation(Param.newAnnotationReference[
					set("value", field.simpleName)
				])
			])
		]
	}*/
	
	//def void createService(MutableClassDeclaration it, extension TransformationContext context) {
		/*val serviceType = findClass(it.qualifiedName + "Service")
		val interfaceType = findClass(it.qualifiedName + "Repository")
		val entityType = it.newSelfTypeReference
		
		serviceType.addAnnotation(Service.newAnnotationReference)
		serviceType.addField("repository") [
			type = interfaceType.newSelfTypeReference
			addAnnotation(Autowired.newAnnotationReference)
		]
			
		serviceType.addMethod("findById") [
			addParameter("id", Serializable.newTypeReference)
			addAnnotation(Transactional.newAnnotationReference[
				setBooleanValue("readOnly", true)
			])
			returnType = entityType
			body = '''
				return repository.findOne(id);
			'''
		]
	}*/
	
	/*override doValidate(ClassDeclaration annotatedClass, extension ValidationContext context) {
		super.doValidate(annotatedClass, context)
		
		annotatedClass.declaredFields.filter [ 
			field | field.hasAnnotation(ManyToOne.findTypeGlobally) && field.hasAnnotation(Column.findTypeGlobally)
		].forEach[ 
			field | field.addError("@Column(s) not allowed on a @ManyToOne property, use @JoinColumn(s)")
		]
	}*/
	
	/*def boolean hasAnnotation(FieldDeclaration it, Type annotationType) {
		findAnnotation(annotationType) !== null
	}*/
	
	/*override doRegisterGlobals(ClassDeclaration annotatedClass, RegisterGlobalsContext context) {
		context.registerInterface(annotatedClass.qualifiedName + "Repository")
		context.registerClass(annotatedClass.qualifiedName + "Service")
	}*/
}
