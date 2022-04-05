/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.NoRedundantInitialOutput;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class NoRedundantInitialOutputTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "behavior/noRedundantInitialOutput";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new NoRedundantInitialOutput());
  }

  @ParameterizedTest
  @ValueSource(strings = {"OneOutputDeclaration.arc", "TwoUnambiguousOutputDeclarations.arc"})
  void shouldNotFindRedundantInitialOutput(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("RedundantOutputDeclaration.arc", ArcError.REDUNDANT_INITIAL_DECLARATION)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFindRedundantInitialOutput(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }
}
