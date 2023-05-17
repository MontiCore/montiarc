/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.MockTestController;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EmptyTest {

  static Context ctx;
  static MockTestController controller;

  @BeforeAll
  static void setUp() {
    controller = new MockTestController();
    controller.init();
    assertThat(controller).isNotNull();

    ctx = controller.getCtx();
    assertThat(ctx).isNotNull();

  }

  public static Stream<Arguments> relations() {

    Expr<IntSort> input = ctx.mkConst("input", ctx.mkIntSort());
    AnnotatedValue<Expr<IntSort>, Integer> in = AnnotatedValue.newAnnoValue(input, 1);

    return Stream.of(
      Arguments.of(in, null)
    );
  }

  @ParameterizedTest
  @MethodSource("relations")
  public void testCompute(@NotNull AnnotatedValue<Expr<IntSort>, Integer> in,
                          @NotNull AnnotatedValue<Expr<IntSort>, Integer> out) {

    Empty comp = new Empty();
    comp.setUp();
    comp.init();
    comp.getIn().update(in);

    comp.compute();

    assertAll(
      () -> assertThat(comp.getIn().getValue()
        .getValue()).isEqualTo(in.getValue()),
      () -> assertThat(comp.getIn().getValue().getExpr()
        .toString()).isEqualTo(in.getExpr().toString()),
      () -> assertThat(comp.getOut().getValue()).isEqualTo(out)
    );
  }
}