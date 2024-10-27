/* (c) https://github.com/MontiCore/monticore */
package de.cd2pojo.modifier;

import de.cd2pojo.modifier.CD1.CPackage;
import de.cd2pojo.modifier.CD1.EPackage;
import de.cd2pojo.modifier.CD1.IPackage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Interfaces, classes, and enums should keep their package private visibility.
 * However, as a workaround, we currently expect them to be public instead.
 */
class CD1Test {

  @Test
  void test_modifier_interface() {
    assertThat(Modifier.isPublic(IPackage.class.getModifiers())).isTrue();
    assertThat(Modifier.isProtected(IPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(IPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isAbstract(IPackage.class.getModifiers())).isTrue();
    assertThat(Modifier.isStatic(IPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isStrict(IPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isInterface(IPackage.class.getModifiers())).isTrue();
  }

  @Test
  void test_modifier_class() {
    assertThat(Modifier.isPublic(CPackage.class.getModifiers())).isTrue();
    assertThat(Modifier.isProtected(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isAbstract(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isStatic(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isFinal(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isStrict(CPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isInterface(CPackage.class.getModifiers())).isFalse();
  }

  @Test
  void test_modifier_enum() {
    assertThat(Modifier.isPublic(EPackage.class.getModifiers())).isTrue();
    assertThat(Modifier.isProtected(EPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isPrivate(EPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isAbstract(EPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isStatic(EPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isFinal(EPackage.class.getModifiers())).isTrue();
    assertThat(Modifier.isStrict(EPackage.class.getModifiers())).isFalse();
    assertThat(Modifier.isInterface(CPackage.class.getModifiers())).isFalse();
  }
}
