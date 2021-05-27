/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.NoSubComponentReferenceCycles;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

public class NoSubComponentReferenceCyclesTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "noSubComponentReferenceCycles";

  @Override
  protected String getPackage() { return PACKAGE; }

  @Override
  protected void registerCoCos() { this.getChecker().addCoCo(new NoSubComponentReferenceCycles()); }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("LongCycle1.arc", ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithDirectSelfReference.arc", ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithNestedSubCompRefCycle.arc", ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithNestedSubCompRefCycle2.arc", ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithSubCompRefCycle.arc", ArcError.NO_SUBCOMPONENT_CYCLE)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindSubComponentRefCycles(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTComponentType ast = this.parseAndLoadAllSymbols(this.getPackage() + "." + model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"DummyWithoutCycle.arc", "WithoutSubCompRefCycle.arc"})
  public void shouldNotFindSubComponentRefCycles(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTComponentType ast = this.parseAndLoadAllSymbols(this.getPackage() + "." + model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  protected ASTComponentType parseAndLoadAllSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    this.getTool().createSymbolTable(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH));
    Preconditions.checkState(MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).isPresent());
    return MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).get().getAstNode();
  }

}
