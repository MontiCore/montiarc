/* (c) https://github.com/MontiCore/monticore */
package de.cd2pojo.modifier;

import de.cd2pojo.modifier.CD1.CPackage;
import de.cd2pojo.modifier.CD1.IPackage;
import de.cd2pojo.modifier.CD2.C1;
import de.cd2pojo.modifier.CD2.C2;
import de.cd2pojo.modifier.CD2.E1;
import de.cd2pojo.modifier.CD2.I1;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Interfaces may extend from interfaces, classes may implement interfaces or
 * extend other classes, and enums may implement interfaces, even if the
 * implemented or extended interfaces, classes, and enums are package private,
 * defined in another classdiagram but in the same package.
 */
class CD2Test {

  @Test
  void test_interface_extends() {
    assertThat(I1.class.getInterfaces()).containsExactly(IPackage.class);
  }

  @Test
  void test_class_implements() {
    assertThat(C1.class.getInterfaces()).containsExactly(IPackage.class);
  }

  @Test
  void test_class_extends() {
    assertThat(C2.class.getSuperclass()).isEqualTo(CPackage.class);
  }

  @Test
  void test_enum_implements() {
    assertThat(E1.class.getInterfaces()).containsExactly(IPackage.class);
  }
}
