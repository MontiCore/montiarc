/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.QualifiedComponentTypeRawType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.RAW_USE_OF_PARAMETRIZED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/** Tests for {@link QualifiedComponentTypeRawType}. */
class QualifiedComponentTypeRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  void setUpComponentTypes() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Valid1 extends a.b.A { }",
    "component Valid2<T> extends a.b.A { }",
    "component Valid3 extends a.b.B<int> { }",
    "component Valid4<T> extends a.b.B<T> { }",
    "component Valid5 extends a.b.A, a.b.A { }",
    "component Valid6<T> extends a.b.A, a.b.A { }",
    "component Valid7 extends a.b.B<int>, a.b.B<double> { }",
    "component Valid8<T> extends a.b.B<T>, a.b.B<int> { }",
    "component Valid9 refines a.b.A { }",
    "component Valid10<T> refines a.b.A { }",
    "component Valid11 refines a.b.B<int> { }",
    "component Valid12<T> refines a.b.B<T> { }",
    "component Valid13 refines a.b.A, a.b.A { }",
    "component Valid14<T> refines a.b.A, a.b.A { }",
    "component Valid15 refines a.b.B<int>, a.b.B<double> { }",
    "component Valid16<T> refines a.b.B<T>, a.b.B<int> { }",
    "component Valid17 { a.b.A sub; }",
    "component Valid18<T> { a.b.A sub; }",
    "component Valid19 { a.b.B<int> sub; }",
    "component Valid20<T> { a.b.B<T> sub; }"
  })
  void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new QualifiedComponentTypeRawType());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void shouldNotDuplicateMissingComponentError() {
    ASTMACompilationUnit ast = compile("component MissingType { Missing sub; }");
    Log.clearFindings();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new QualifiedComponentTypeRawType());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new QualifiedComponentTypeRawType());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Invalid1 extends a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid2<T> extends a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid3<T> extends a.b.A, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid4 extends a.b.B, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE, RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid5 refines a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid6<T> refines a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid7<T> refines a.b.A, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid8 refines a.b.B, a.b.B { }",
        RAW_USE_OF_PARAMETRIZED_TYPE, RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid9 { a.b.B sub; }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid10<T> { a.b.B sub; }",
        RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Invalid11 { a.b.B first, second; }",
        RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
