/* (c) https://github.com/MontiCore/monticore */
package montiarc.compnames;

import montiarc.rte.component.Component;
import montiarc.rte.component.SimComponent;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JSimTest
class CompNamesTest {

  @Test
  void testNamesInMode1() {
    // Given && When
    RootCompCompImpl sut = (RootCompCompImpl) new RootCompCompBuilder().setName("sut").build();

    // Then
    assertAll(
      () -> assertThat(sut.getName()).isEqualTo("sut"),
      () -> assertThat(sut.getAllSubcomponents())
              .map(Component::getName)
              .containsExactlyInAnyOrder("sut.directLeaf", "sut.withModes")
    );

    SimComponent modeSubComp =
      sut.getAllSubcomponents()
        .stream().filter(WithModesCompImpl.class::isInstance)
        .findFirst().orElseThrow();

    assertThat(modeSubComp.getAllSubcomponents())
      .map(Component::getName)
      .containsExactlyInAnyOrder(
        "sut.withModes.alwaysPresent",
        "sut.withModes.M1.inMode1",
        "sut.withModes.M1.sharedNameInModes"
      );
  }

  @Test
  void testNamesInMode2() {
    // Given
    RootCompCompImpl sut = (RootCompCompImpl) new RootCompCompBuilder().setName("sut").build();

    WithModesCompImpl modeSubComp =
      sut.getAllSubcomponents()
        .stream()
        .filter(WithModesCompImpl.class::isInstance)
        .map(WithModesCompImpl.class::cast)
        .findFirst().orElseThrow();

    // When we switch to mode 2
    modeSubComp.port_i().receive(tk());
    modeSubComp.handleTick();
    modeSubComp.handleTickReconfiguration();

    // Then
    assertAll(
      () -> assertThat(sut.getName()).isEqualTo("sut"),
      () -> assertThat(sut.getAllSubcomponents())
        .map(Component::getName)
        .containsExactlyInAnyOrder("sut.directLeaf", "sut.withModes")
    );

    assertThat(modeSubComp.getAllSubcomponents())
      .map(Component::getName)
      .containsExactlyInAnyOrder(
        "sut.withModes.alwaysPresent",
        "sut.withModes.M2.inMode2",
        "sut.withModes.M2.sharedNameInModes"
      );
  }
}
