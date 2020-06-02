package ru.jpoint.xtend.demo

import org.eclipse.xtend.core.compiler.batch.XtendCompilerTester
import org.junit.Test

class GeneratorTest {
		
	extension XtendCompilerTester compilerTester = XtendCompilerTester.newXtendCompilerTester(class.classLoader)
	
	@Test
    def void testGenerator() {
    	'''
    	package ru.jpoint.boot.demo.domain
    	
    	import javax.persistence.Column
    	import javax.persistence.Id
    	import javax.persistence.ManyToOne
    	import javax.persistence.Transient
    	import org.eclipse.xtend.lib.annotations.EqualsHashCode
    	import org.eclipse.xtend.lib.annotations.ToString
    	import javax.persistence.JoinColumn
    	import ru.jpoint.xtend.demo.Entity
    	import org.eclipse.xtend.lib.annotations.Accessors
    	
    	@Accessors
    	@Entity
    	@ToString
    	@EqualsHashCode
    	class Country<Country> {
    		
    		@Id 
    		Long id
    		
    		def void setId(Long id) {
    	    	this.id = id;
    	  	}
    	
    		@Transient
    		transient boolean isNew
    		
    		@Column(nullable = false) 
    		String name
    	}
    	'''.compile [
    		println(allGeneratedResources)
    	]
    }
}