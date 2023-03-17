/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._cocos;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstantiation;
import comfortablearc.AbstractTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTArcACMode;
import comfortablearc._ast.ASTArcAutoConnect;
import montiarc.util.ComfortableArcError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class AtomicNoAutoConnectTest extends AbstractTest {

  @Test
  void shouldAllowNoAutoConnectInAtomicComponent() {
    // Given
    ASTComponentBody body = ComfortableArcMill.componentBodyBuilder().build();
    AtomicNoAutoConnect coco = new AtomicNoAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(/* none */);
  }

  @Test
  void shouldAllowNoAutoConnectInComposition() {
    // Given
    ASTComponentBody body = ComfortableArcMill
      .componentBodyBuilder()
      .addArcElement(Mockito.mock(ASTComponentInstantiation.class))
      .build();
    AtomicNoAutoConnect coco = new AtomicNoAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(/* none */);
  }

  @ParameterizedTest
  @MethodSource("provideDifferentAutoConnectVariations")
  void shouldAllowAutoConnectInComposition(Supplier<ASTArcAutoConnect> autoconnectSupplier) {
    // Given
    ASTComponentBody body = ComfortableArcMill
      .componentBodyBuilder()
      .addArcElement(autoconnectSupplier.get())
      .addArcElement(Mockito.mock(ASTComponentInstantiation.class))
      .build();
    AtomicNoAutoConnect coco = new AtomicNoAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(/* none */);
  }

  @ParameterizedTest
  @MethodSource("provideDifferentAutoConnectVariations")
  void shouldViolateAutoConnectInAtomicComponent(Supplier<ASTArcAutoConnect> autoconnectSupplier) {
    // Given
    ASTComponentBody body = ComfortableArcMill
      .componentBodyBuilder()
      .addArcElement(autoconnectSupplier.get())
      .build();
    AtomicNoAutoConnect coco = new AtomicNoAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT);
  }

  static Stream<Arguments> provideDifferentAutoConnectVariations() {
    // We use suppliers, because we can not call the mill yet. It will not be initialized yet.
    Supplier<ASTArcACMode> acOff = () -> ComfortableArcMill.arcACOffBuilder().build();
    Supplier<ASTArcACMode> acType = () -> ComfortableArcMill.arcACTypeBuilder().build();
    Supplier<ASTArcACMode> acPort = () -> ComfortableArcMill.arcACPortBuilder().build();

    return Stream.of(
      Arguments.of(buildAutoConnectDeclaration(acOff)),
      Arguments.of(buildAutoConnectDeclaration(acType)),
      Arguments.of(buildAutoConnectDeclaration(acPort))
    );
  }

  private static Supplier<ASTArcAutoConnect> buildAutoConnectDeclaration(Supplier<ASTArcACMode> modeSupplier) {
    return () ->
      ComfortableArcMill
        .arcAutoConnectBuilder()
        .setArcACMode(modeSupplier.get())
        .build();
  }
}
