package ru.jpoint.xtend.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.eclipse.xtend.lib.macro.Active;

@Target(ElementType.TYPE)
@Active(JavaEntityProcessor.class)
public @interface Entity {
}
