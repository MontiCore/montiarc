/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.tests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for test classes and methods that test generated MA2JSim code.
 * <br>
 * Applies the {@link JSimLogExtension}. As the static SE-logger is involved,
 * parallel execution ist locked.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(JSimLogExtension.class)
@ResourceLock(value = "de.se_rwth.commons.Log")
public @interface JSimTest { }
