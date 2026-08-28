/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTypeFits;
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

import static montiarc.util.ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH;
import static montiarc.util.ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortHeritageTypeFits}.
 */
class PortHeritageTypeFitsTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; port out int o; }");
    compile("package a.b; component C<T> { port in T i; port out T o; } ");
    compile("package a.b; component D { port in int i; }");
    compile("package a.b; component E { port out int o; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no heritage
    "component ValidComp1 { }",
    // heritage without ports
    "component ValidComp2 extends a.b.A { }",
    // heritage adding ports, supertype has no ports
    "component ValidComp3 extends a.b.A { port in int i; port out int o; }",
    // heritage without additional ports, inheriting all super ports
    "component ValidComp4 extends a.b.B { }",
    // heritage adding new ports alongside inherited super ports
    "component ValidComp5 extends a.b.B { port in int i2; port out int o2; }",
    // heritage overriding super ports with the same type
    "component ValidComp6 extends a.b.B { port in int i; port out int o; }",
    // heritage overriding the incoming super port with a subtype
    "component ValidComp7 extends a.b.B { port in byte i; port out int o; }",
    // heritage overriding the outgoing super port with a supertype
    "component ValidComp8 extends a.b.B { port in int i; port out long o; }",
    // heritage overriding both incoming and outgoing super ports compatibly
    "component ValidComp9 extends a.b.B { port in byte i; port out long o; }",
    // heritage overriding generic typed ports with a matching bound type
    "component ValidComp10 extends a.b.C<int> { port in int i; port out int o; }",
    // heritage overriding generic typed ports with the same type parameter
    "component ValidComp11<T> extends a.b.C<T> { port in T i; port out T o; }",
    // multi-heritage overriding the outgoing super port with a supertype
    "component ValidComp12 extends a.b.D, a.b.E { port in int i; port out long o; }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTypeFits());

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
    checker.addCoCo(new PortHeritageTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // heritage overriding the incoming super port with a supertype
      arg("component InvalidComp1 extends a.b.B { port in double i; port out int o; }",
        HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage overriding the outgoing super port with a subtype
      arg("component InvalidComp2 extends a.b.B { port in int i; port out byte o; }",
        HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage with both incoming and outgoing port type mismatches
      arg("component InvalidComp3 extends a.b.B { port in double i; port out byte o; }",
        HERITAGE_IN_PORT_TYPE_MISMATCH,
        HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage overriding a generic typed incoming port with an incompatible type
      arg("component InvalidComp4 extends a.b.C<int> { port in double i; port out int o; }",
        HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage overriding a generic typed outgoing port with an incompatible type
      arg("component InvalidComp5 extends a.b.C<int> { port in int i; port out byte o; }",
        HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // multi-heritage with both incoming and outgoing port type mismatches
      arg("component InvalidComp6 extends a.b.D, a.b.E { port in double i; port out byte o; }",
        HERITAGE_IN_PORT_TYPE_MISMATCH,
        HERITAGE_OUT_PORT_TYPE_MISMATCH)
    );
  }
}
