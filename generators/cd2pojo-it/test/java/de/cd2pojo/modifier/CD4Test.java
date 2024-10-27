/* (c) https://github.com/MontiCore/monticore */
package de.cd2pojo.modifier;

import de.cd2pojo.modifier.CD4.C6;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * We currently require that fields derived from associations have public
 * visibility in the generated java code, as we are not able to replace field
 * access with method calls in complex expressions.
 */
class CD4Test {

  @Test
  @Disabled
  void test_assoc_modifiers() throws NoSuchFieldException {
    assertThat(Modifier.isPublic(C6.class.getDeclaredField("a").getModifiers())).isTrue();
    assertThat(Modifier.isProtected(C6.class.getDeclaredField("a").getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(C6.class.getDeclaredField("a").getModifiers())).isFalse();
    assertThat(Modifier.isStatic(C6.class.getDeclaredField("a").getModifiers())).isFalse();
    assertThat(Modifier.isFinal(C6.class.getDeclaredField("a").getModifiers())).isFalse();
    assertThat(Modifier.isTransient(C6.class.getDeclaredField("a").getModifiers())).isFalse();
    assertThat(Modifier.isVolatile(C6.class.getDeclaredField("a").getModifiers())).isFalse();
  }
}
