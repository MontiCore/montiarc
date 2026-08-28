/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import arcbasis._cocos.SubcomponentRawType;
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
 * The class under test is {@link SubcomponentRawType}.
 */
class SubcomponentRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // subcomponent instance of a non-generic type
    "component ValidComp1 { a.b.A sub; }",
    // generic component with a subcomponent instance of a non-generic type
    "component ValidComp2<T> { a.b.A sub; }",
    // subcomponent instance of a generic type with a concrete type argument
    "component ValidComp3 { a.b.B<int> sub; }",
    // generic component with a subcomponent instance of a generic type using its own type parameter as argument
    "component ValidComp4<T> { a.b.B<T> sub; }"
  })
  void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportErrors(@NotNull String model,
                          @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // raw use of a generic type as a subcomponent instance
      arg("component InvalidComp1 { a.b.B sub; }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      // generic component with a raw use of a generic type as a subcomponent instance
      arg("component InvalidComp2<T> { a.b.B sub; }",
        RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
