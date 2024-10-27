/* (c) https://github.com/MontiCore/monticore */
package de.cd2pojo.modifier;

import de.cd2pojo.modifier.CD3.C4;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * We currently require that fields have the same visibility in the generated
 * java code as in the classdiagram, as we are not able to replace field access
 * with method calls in complex expressions.
 */
class CD3Test {

  @Test
  void test_field_package_modifiers() throws NoSuchFieldException {
    assertThat(Modifier.isPublic(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isProtected(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isStatic(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isFinal(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isTransient(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
    assertThat(Modifier.isVolatile(C4.class.getDeclaredField("a1").getModifiers())).isFalse();
  }

  @Test
  void test_field_public_modifiers() throws NoSuchFieldException {
    assertThat(Modifier.isPublic(C4.class.getDeclaredField("a2").getModifiers())).isTrue();
    assertThat(Modifier.isProtected(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
    assertThat(Modifier.isStatic(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
    assertThat(Modifier.isFinal(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
    assertThat(Modifier.isTransient(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
    assertThat(Modifier.isVolatile(C4.class.getDeclaredField("a2").getModifiers())).isFalse();
  }

  @Test
  void test_field_protected_modifiers() throws NoSuchFieldException {
    assertThat(Modifier.isPublic(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
    assertThat(Modifier.isProtected(C4.class.getDeclaredField("a3").getModifiers())).isTrue();
    assertThat(Modifier.isPrivate(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
    assertThat(Modifier.isStatic(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
    assertThat(Modifier.isFinal(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
    assertThat(Modifier.isTransient(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
    assertThat(Modifier.isVolatile(C4.class.getDeclaredField("a3").getModifiers())).isFalse();
  }

  @Test
  void test_field_private_modifiers() throws NoSuchFieldException {
    assertThat(Modifier.isPublic(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
    assertThat(Modifier.isProtected(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(C4.class.getDeclaredField("a4").getModifiers())).isTrue();
    assertThat(Modifier.isStatic(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
    assertThat(Modifier.isFinal(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
    assertThat(Modifier.isTransient(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
    assertThat(Modifier.isVolatile(C4.class.getDeclaredField("a4").getModifiers())).isFalse();
  }
}
