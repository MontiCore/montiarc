/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FieldInitTypeFits;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link FieldInitTypeFits}.
 */
public class FieldInitTypeFitsTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // field init primitive
    "component Comp1 { " +
      "boolean var1 = true; " +
      "int var2 = 0; " +
      "long var3 = 0L; " +
      "}",
    // field to parameter assignment
    "component Comp2(boolean p1, int p2) { " +
      "boolean var1 = p1; " +
      "int var2 = p2; " +
      "}",
    // field init primitive (inner)
    "component Comp3 { " +
      "component Inner { " +
      "boolean var1 = true; " +
      "int var2 = 0; " +
      "long var3 = 0L; " +
      "} " +
      "}",
    // field to parameter assignment (inner)
    "component Comp4 { " +
      "component Inner(boolean p1, int p2) { " +
      "boolean var1 = p1; " +
      "int var2 = p2; " +
      "} " +
      "}",
    // variable (parameter) shadowing
    "component Comp5(boolean p) { " +
      "boolean var = p; " +
      "component Inner(int p) { " +
      "int var = p; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FieldInitTypeFits(new MontiArcTypeCalculator(), new TypeRelations()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FieldInitTypeFits(new MontiArcTypeCalculator(), new TypeRelations()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // field init primitive type mismatch
      arg("component Comp1 { " +
          "boolean var = 1; " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // field two primitives type mismatch
      arg("component Comp2 { " +
          "boolean var1 = 2; " +
          "int var2 = false; " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH,
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // field to parameter assignment type mismatch
      arg("component Comp3(int p) { " +
          "boolean var = p; " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // two fields to parameter assignments type mismatch
      arg("component Comp4(boolean p1, int p2) { " +
          "boolean var1 = p2; " +
          "int var2 = p1; " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH,
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // field init primitive (inner) type mismatch
      arg("component Comp5 { " +
          "component Inner { " +
          "boolean var = 1; " +
          "} " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // field to parameter assignment (inner) type mismatch
      arg("component Comp5 { " +
          "component Inner(int p) { " +
          "boolean var = p; " +
          "} " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // variable (parameter) shadowing type mismatch
      arg("component Comp6(boolean p) { " +
          "boolean var = p; " +
          "component Inner(boolean p) { " +
          "int var = p; " +
          "} " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH),
      // variable (parameter) shadowing type mismatch
      arg("component Comp7(int p) { " +
          "boolean var = p; " +
          "component Inner(boolean p) { " +
          "int var = p; " +
          "} " +
          "}",
        ArcError.FIELD_INIT_TYPE_MISMATCH,
        ArcError.FIELD_INIT_TYPE_MISMATCH)
    );
  }
}
