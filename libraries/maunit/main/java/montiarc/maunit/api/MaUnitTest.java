/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Testable
public @interface MaUnitTest {

  Class<? extends MaUnitTestContext> value();
}
