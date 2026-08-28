/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.RefinementRawType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.RAW_USE_OF_PARAMETRIZED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link RefinementRawType}.
 */
class RefinementRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpSuperTypes() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // refines a non-generic supertype
    "component ValidComp1 refines a.b.A { }",
    // generic subtype refines a non-generic supertype
    "component ValidComp2<T> refines a.b.A { }",
    // refines a generic supertype with a concrete type argument
    "component ValidComp3 refines a.b.B<int> { }",
    // generic subtype refines a generic supertype with its own type parameter as argument
    "component ValidComp4<T> refines a.b.B<T> { }",
    // refines the same non-generic supertype twice
    "component ValidComp5 refines a.b.A, a.b.A { }",
    // generic subtype refines the same non-generic supertype twice
    "component ValidComp6<T> refines a.b.A, a.b.A { }",
    // refines a generic supertype twice with different concrete type arguments
    "component ValidComp7 refines a.b.B<int>, a.b.B<double> { }",
    // generic subtype refines a generic supertype once with its own type parameter, once with a concrete type argument
    "component ValidComp8<T> refines a.b.B<T>, a.b.B<int> { }",
  })
  void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
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
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // raw use of a generic supertype
      arg("component InvalidComp1 refines a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      // generic subtype with a raw use of a generic supertype
      arg("component InvalidComp2<T> refines a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      // generic subtype refines a non-generic supertype and a raw use of a generic supertype
      arg("component InvalidComp3<T> refines a.b.A, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      // raw use of the same generic supertype twice
      arg("component InvalidComp4 refines a.b.B, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE,
        RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
