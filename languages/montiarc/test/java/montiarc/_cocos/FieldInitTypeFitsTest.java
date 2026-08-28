/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FieldInitTypeFits;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_INIT_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FieldInitTypeFits}.
 */
class FieldInitTypeFitsTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // fields initialized with matching primitive literals
    "component ValidComp1 { boolean var1 = true; int var2 = 0; long var3 = 0L; }",
    // fields initialized from matching parameters
    "component ValidComp2(boolean p1, int p2) { boolean var1 = p1; int var2 = p2; }",
    // nested component type with fields initialized with matching primitive literals
    "component ValidComp3 { component Inner { boolean var1 = true; int var2 = 0; long var3 = 0L; } }",
    // nested component type with fields initialized from matching parameters
    "component ValidComp4 { component Inner(boolean p1, int p2) { boolean var1 = p1; int var2 = p2; } }",
    // same-named parameter with a different type shadowed correctly across nesting levels
    "component ValidComp5(boolean p) { boolean var = p; component Inner(int p) { int var = p; } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FieldInitTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FieldInitTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // field initialized with a mismatching primitive literal
      arg("component InvalidComp1 { boolean var = 1; }",
        FIELD_INIT_TYPE_MISMATCH),
      // two fields, both initialized with mismatching primitive literals
      arg("component InvalidComp2 { boolean var1 = 2; int var2 = false; }",
        FIELD_INIT_TYPE_MISMATCH,
        FIELD_INIT_TYPE_MISMATCH),
      // field initialized from a mismatching parameter
      arg("component InvalidComp3(int p) { boolean var = p; }",
        FIELD_INIT_TYPE_MISMATCH),
      // two fields, both initialized from mismatching (swapped) parameters
      arg("component InvalidComp4(boolean p1, int p2) { boolean var1 = p2; int var2 = p1; }",
        FIELD_INIT_TYPE_MISMATCH,
        FIELD_INIT_TYPE_MISMATCH),
      // nested component type with a field initialized with a mismatching primitive literal
      arg("component InvalidComp5 { component Inner { boolean var = 1; } }",
        FIELD_INIT_TYPE_MISMATCH),
      // nested component type with a field initialized from a mismatching parameter
      arg("component InvalidComp6 { component Inner(int p) { boolean var = p; } }",
        FIELD_INIT_TYPE_MISMATCH),
      // nested component type with its own same-named parameter mismatching its field's type
      arg("component InvalidComp7(boolean p) { boolean var = p; component Inner(boolean p) { int var = p; } }",
        FIELD_INIT_TYPE_MISMATCH),
      // outer and nested component type each with their own same-named parameter mismatching their field's type
      arg("component InvalidComp8(int p) { boolean var = p; component Inner(boolean p) { int var = p; } }",
        FIELD_INIT_TYPE_MISMATCH,
        FIELD_INIT_TYPE_MISMATCH)
    );
  }
}
