/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UnsupportedAutomatonElementsTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "unsupportedAutomatonElements";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new UnsupportedAutomatonElements.EntryActions());
    checker.addCoCo(new UnsupportedAutomatonElements.ExitActions());
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    checker.addCoCo(new UnsupportedAutomatonElements.AutomatonStereotypes());
  }


  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("HasEntryAction.arc", ArcError.UNSUPPORTED_MODEL_ELEMENT),
      arg("HasExitAction.arc", ArcError.UNSUPPORTED_MODEL_ELEMENT),
      arg("HasFinalState.arc", ArcError.UNSUPPORTED_MODEL_ELEMENT),
      arg("HasStereotypedAutomaton.arc", ArcError.UNSUPPORTED_MODEL_ELEMENT)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  protected void shouldFindUnsupportedAutomatonElements(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }
}
