package com.example.demo;

import org.eclipse.xtend.core.compiler.batch.XtendCompilerTester;
import org.eclipse.xtext.xbase.lib.Extension;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class GeneratorTest {
  private final static Logger logger = LoggerFactory.getLogger(GeneratorTest.class);
  
  @Extension
  private XtendCompilerTester compilerTester = XtendCompilerTester.newXtendCompilerTester(this.getClass().getClassLoader());
  
  @Test
  public void testXFWBlobInfoDefault() {
  }
}
