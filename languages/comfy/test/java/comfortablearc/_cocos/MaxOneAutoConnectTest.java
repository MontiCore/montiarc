/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._cocos;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentBodyBuilder;
import comfortablearc.ComfortableArcAbstractTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTArcAutoConnect;
import montiarc.util.ComfortableArcError;
import montiarc.util.Error;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static montiarc.util.ComfortableArcError.MULTIPLE_AUTOCONNECTS;

public class MaxOneAutoConnectTest extends ComfortableArcAbstractTest {

  @Test
  void shouldAllowNoAutoconnect() {
    // Given
    ASTComponentBody body = ComfortableArcMill.componentBodyBuilder().build();
    MaxOneAutoConnect coco = new MaxOneAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(/* none */);
  }

  @ParameterizedTest
  @ValueSource(strings = {"off", "port", "type"})
  void shouldAllowOneAutoconnect(String mode) {
    // Given
    ASTComponentBody body = buildCompBodyWithAutoconnects(mode);
    MaxOneAutoConnect coco = new MaxOneAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(/* none */);
  }

  @ParameterizedTest
  @MethodSource("multipleAutoconnectProvider")
  void shouldNotAllowMultipleAutoconnects(String[] modes, Error[] expectedErrors) {
    // Given
    ASTComponentBody body = buildCompBodyWithAutoconnects(modes);
    MaxOneAutoConnect coco = new MaxOneAutoConnect();

    // When
    coco.check(body);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }

  protected static Stream<Arguments> multipleAutoconnectProvider() {
    String[] variants = new String[] {"off", "port", "type"};

    Stream.Builder<Arguments> argBuilder =  Stream.builder();

    // Build all possible 2-combinations
    for(String variant1 : variants) {
      for(String variant2 : variants) {
        argBuilder.add(
          Arguments.of(new String[] {variant1, variant2}, new ComfortableArcError[] {MULTIPLE_AUTOCONNECTS})
        );
      }
    }

    argBuilder.add(
      Arguments.of(new String[] {"off", "port", "type", }, new ComfortableArcError[] {MULTIPLE_AUTOCONNECTS})
    );

    return argBuilder.build();
  }

  /**
   * @param modes every mode is allowed to be "off", "port", or "type" (others throw IllegalArgumentExceptions)
   */
  protected static ASTComponentBody buildCompBodyWithAutoconnects(String... modes) {
    ASTComponentBodyBuilder bodyBuilder = ComfortableArcMill.componentBodyBuilder();

    Arrays.stream(modes)
      .map(MaxOneAutoConnectTest::buildAutoconnectMode)
      .forEach(bodyBuilder::addArcElement);

    return bodyBuilder.build();
  }

  /**
   * @param modeName "off", "port", or "type" (others throw IllegalArgumentExceptions)
   */
  protected static ASTArcAutoConnect buildAutoconnectMode(String modeName) {
    switch (modeName) {
      case "off": return ComfortableArcMill.arcAutoConnectBuilder().setArcACModeOff().build();
      case "port": return ComfortableArcMill.arcAutoConnectBuilder().setArcACModePort().build();
      case "type": return ComfortableArcMill.arcAutoConnectBuilder().setArcACModeType().build();
      default: throw new IllegalArgumentException();
    }
  }
}
