/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._cocos.ComponentHeritageRawType;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentHeritageRawTypeTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpSuperTypes() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 extends a.b.A { }",
    "component Comp2<T> extends a.b.A { }",
    "component Comp3 extends a.b.B<int> { }",
    "component Comp4<T> extends a.b.B<T> { }",
    "component Comp5 extends a.b.A, a.b.A { }",
    "component Comp6<T> extends a.b.A, a.b.A { }",
    "component Comp7 extends a.b.B<int>, a.b.B<double> { }",
    "component Comp8<T> extends a.b.B<T>, a.b.B<int> { }",
  })
  public void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentHeritageRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentHeritageRawType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 extends a.b.B { }",
        GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp2<T> extends a.b.B { }",
        GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp3<T> extends a.b.A, a.b.B { }",
        GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE),
      arg("component Comp4 extends a.b.B, a.b.B { }",
        GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE,
        GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE)
    );
  }
}
