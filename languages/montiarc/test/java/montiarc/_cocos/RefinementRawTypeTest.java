/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.RefinementRawType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link RefinementRawType} CoCo.
 */
class RefinementRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpSuperTypes() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 refines a.b.A { }",
    "component Comp2<T> refines a.b.A { }",
    "component Comp3 refines a.b.B<int> { }",
    "component Comp4<T> refines a.b.B<T> { }",
    "component Comp5 refines a.b.A, a.b.A { }",
    "component Comp6<T> refines a.b.A, a.b.A { }",
    "component Comp7 refines a.b.B<int>, a.b.B<double> { }",
    "component Comp8<T> refines a.b.B<T>, a.b.B<int> { }",
  })
  void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 refines a.b.B { }",
        ArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp2<T> refines a.b.B { }",
        ArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp3<T> refines a.b.A, a.b.B { }",
        ArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp4 refines a.b.B, a.b.B { }",
        ArcError.RAW_USE_OF_PARAMETRIZED_TYPE,
        ArcError.RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
