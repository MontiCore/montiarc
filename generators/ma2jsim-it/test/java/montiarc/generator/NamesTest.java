/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class NamesTest {

  @Test
  void testNames4Leaf() {
    // Given && When
    LeafComp sut = new LeafCompBuilder().setName("sut").build();

    // Then
    assertThat(sut.getName()).isEqualTo("sut");
    assertThat(sut.port_i1().getQualifiedName()).isEqualTo("sut.i1");
    assertThat(sut.port_i2().getQualifiedName()).isEqualTo("sut.i2");
    assertThat(sut.port_o1().getQualifiedName()).isEqualTo("sut.o1");
    assertThat(sut.port_o2().getQualifiedName()).isEqualTo("sut.o2");
  }

  @Test
  void testNames4ModeM1() {
    // Given && When
    Branch2CompImpl sut = (Branch2CompImpl) new Branch2CompBuilder().setName("sut").build();

    // Then
    assertThat(sut.getName()).isEqualTo("sut");
    assertThat(sut.subcomp_any().getName()).isEqualTo("sut.any");
    assertThat(sut.subcomp_M1_only1().getName()).isEqualTo("sut.M1.only1");
    assertThat(sut.subcomp_M1_shared().getName()).isEqualTo("sut.M1.shared");
  }

  @Test
  void testNames4ModeM2() {
    // Given && When
    Branch2CompImpl sut = (Branch2CompImpl) new Branch2CompBuilder().setName("sut").build();
    sut.teardownMode_M1();
    sut.setupMode_M2();

    // Then
    assertThat(sut.getName()).isEqualTo("sut");
    assertThat(sut.subcomp_any().getName()).isEqualTo("sut.any");
    assertThat(sut.subcomp_M2_only2().getName()).isEqualTo("sut.M2.only2");
    assertThat(sut.subcomp_M2_shared().getName()).isEqualTo("sut.M2.shared");
  }

  @Test
  void testNames4Branch() {
    // Given && When
    Branch1CompImpl sut = (Branch1CompImpl) new Branch1CompBuilder().setName("sut").build();

    // Then
    assertThat(sut.getName()).isEqualTo("sut");
    assertThat(sut.port_i1().getQualifiedName()).isEqualTo("sut.i1");
    assertThat(sut.port_i2().getQualifiedName()).isEqualTo("sut.i2");
    assertThat(sut.port_o1().getQualifiedName()).isEqualTo("sut.o1");
    assertThat(sut.port_o2().getQualifiedName()).isEqualTo("sut.o2");
    assertThat(sut.subcomp_n1().getName()).isEqualTo("sut.n1");
    assertThat(sut.subcomp_n1().port_i1().getQualifiedName()).isEqualTo("sut.n1.i1");
    assertThat(sut.subcomp_n2().getName()).isEqualTo("sut.n2");
    assertThat(sut.subcomp_n2().port_i1().getQualifiedName()).isEqualTo("sut.n2.i1");
    assertThat(sut.subcomp_n2().subcomp_any().getName()).isEqualTo("sut.n2.any");
  }
}
