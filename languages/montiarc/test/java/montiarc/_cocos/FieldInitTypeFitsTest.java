/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.TypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

public class FieldInitTypeFitsTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "fieldInitExpressionTypesCorrect";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new FieldInitTypeFits(new MontiArcTypeCalculator(), new TypeRelations()));
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    this.provideTypes();
  }

  protected void provideTypes() {
    TypeSymbol string = MontiArcMill.typeSymbolBuilder()
      .setName("String")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();

    TypeSymbol person = MontiArcMill.typeSymbolBuilder()
      .setName("Person")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();

    SymbolService.link(MontiArcMill.globalScope(), string);
    SymbolService.link(MontiArcMill.globalScope(), person);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("IncorrectFieldInitializations.arc",
        ArcError.FIELD_INIT_TYPE_MISMATCH,
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      arg("IncompatibleAndTypeRef.arc",
        ArcError.FIELD_INIT_TYPE_REF,
        ArcError.FIELD_INIT_TYPE_MISMATCH,
        ArcError.FIELD_INIT_TYPE_REF,
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      arg("TypeRefAsFieldInitialization.arc",
        ArcError.FIELD_INIT_TYPE_REF,
        ArcError.FIELD_INIT_TYPE_REF)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "CorrectFieldInitializations.arc",
      "FieldInitializationWithConstructor.arc",
      "ParameterShadowing.arc"
  })
  void shouldApproveValidTypeAssignments(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH)));
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFindInvalidTypeAssignments(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}