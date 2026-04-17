/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentHeritageRawType;
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

import java.util.stream.Stream;

import static montiarc.util.ArcError.RAW_USE_OF_PARAMETRIZED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ComponentHeritageRawType}.
 */
class ComponentHeritageRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  void setUpSuperTypes() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentHeritageRawType());

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
    checker.addCoCo(new ComponentHeritageRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // extends a non-generic supertype
      arg("component ValidComp1 extends a.b.A { }"),
      // generic subtype extends a non-generic supertype
      arg("component ValidComp2<T> extends a.b.A { }"),
      // extends a generic supertype with a concrete type argument
      arg("component ValidComp3 extends a.b.B<int> { }"),
      // generic subtype extends a generic supertype with its own type parameter as argument
      arg("component ValidComp4<T> extends a.b.B<T> { }"),
      // extends the same non-generic supertype twice
      arg("component ValidComp5 extends a.b.A, a.b.A { }"),
      // generic subtype extends the same non-generic supertype twice
      arg("component ValidComp6<T> extends a.b.A, a.b.A { }"),
      // extends a generic supertype twice with different concrete type arguments
      arg("component ValidComp7 extends a.b.B<int>, a.b.B<double> { }"),
      // generic subtype extends a generic supertype once with its own type parameter, once with a concrete type argument
      arg("component ValidComp8<T> extends a.b.B<T>, a.b.B<int> { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // raw use of a generic supertype
      arg("component InvalidComp1 extends a.b.B { }", RAW_USE_OF_PARAMETRIZED_TYPE),
      // generic subtype with a raw use of a generic supertype
      arg("component InvalidComp2<T> extends a.b.B { }", RAW_USE_OF_PARAMETRIZED_TYPE),
      // generic subtype extends a non-generic supertype and a raw use of a generic supertype
      arg("component InvalidComp3<T> extends a.b.A, a.b.B { }", RAW_USE_OF_PARAMETRIZED_TYPE),
      // raw use of the same generic supertype twice
      arg("component InvalidComp4 extends a.b.B, a.b.B { }", RAW_USE_OF_PARAMETRIZED_TYPE, RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
