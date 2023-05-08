/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import dse.DSE;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.strategie.MockPathCoverageController;
import montiarc.rte.dse.strategie.PathCoverageController;
import montiarc.rte.timesync.IComponent;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ComponentsTest {

  static Context ctx;

  @BeforeAll
  static void setUp() throws Exception {
    PathCoverageController<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller =
      MockPathCoverageController.init(DSE::runOnce);
    ctx = TestController.getCtx();
    assertThat(ctx).isNotNull();
  }

  @ParameterizedTest
  @MethodSource("relations")
  public void testCompute(@NotNull AnnotatedValue<Expr<IntSort>, Integer> in,
                          @NotNull AnnotatedValue<Expr<IntSort>, Integer> out,
                          @NotNull IComponent comp) {

    assertThat(comp instanceof Empty || comp instanceof Simple || comp instanceof Composition).isEqualTo(true);

    comp.setUp();
    comp.init();

    if (comp instanceof Empty) ((Empty) comp).getIn().update(in);
    else if (comp instanceof Simple) ((Simple) comp).getIn().update(in);
    else if (comp instanceof Composition) ((Composition) comp).getIn().update(in);

    comp.compute();
    if (comp instanceof Empty) {
      assertAll(
        () -> assertThat(((Empty) comp).getIn().getValue().getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((Empty) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((Empty) comp).getOut().getValue()).isEqualTo(out)
      );
    }
    else if (comp instanceof Simple) {
      assertAll(
        () -> assertThat(((Simple) comp).getIn().getValue().getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((Simple) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((Simple) comp).getOut().getValue().getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((Simple) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else {
      assertAll(
        () -> assertThat(((Composition) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((Composition) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((Composition) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((Composition) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
  }

  public static Stream<Arguments> relations() {

    Expr<IntSort> input = ctx.mkConst("input", ctx.mkIntSort());
    AnnotatedValue<Expr<IntSort>, Integer> in = AnnotatedValue.newAnnoValue(input, 1);
    Empty empty = new Empty();
    Simple simple = new Simple();
    Composition composition = new Composition();

    return Stream.of(
      Arguments.of(in, null, empty),
      Arguments.of(in, in, simple),
      Arguments.of(in, in, composition)
    );
  }


  @ParameterizedTest
  @MethodSource("histories")
  public void testCompute(@NotNull Integer[] in,
                          @NotNull Expr[] exprIn,
                          @NotNull Integer[] out,
                          @NotNull Expr[] exprOut,
                          @NotNull IComponent comp) {

    Preconditions.checkNotNull(in);
    Preconditions.checkArgument(in.length > 0);
    Preconditions.checkArgument(exprIn.length == in.length);
    Preconditions.checkArgument(out.length == in.length);
    Preconditions.checkArgument(exprOut.length == in.length);

    assertThat(comp instanceof Simple || comp instanceof Composition).isEqualTo(true);

    comp.setUp();
    comp.init();

    Integer[] actOut = new Integer[out.length];
    Expr<IntSort>[] actExprOut = new Expr[out.length];

    if (comp instanceof Simple) {
      for (int j = 0; j < in.length; j++) {
        ((Simple) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));

        comp.compute();

        actOut[j] = ((Simple) comp).getOut().getValue().getValue();
        actExprOut[j] = ((Simple) comp).getOut().getValue().getExpr();
      }
    }
    else if (comp instanceof Composition) {
      for (int j = 0; j < in.length; j++) {
        ((Composition) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));

        comp.compute();

        actOut[j] = ((Composition) comp).getOut().getValue().getValue();
        actExprOut[j] = ((Composition) comp).getOut().getValue().getExpr();
      }
    }

    assertAll(
      () -> assertThat(actOut).containsExactly(out),
      () -> assertThat(actExprOut).containsExactly(exprOut)

    );
  }

  public static Stream<Arguments> histories() {

    Expr<IntSort> input_0 = ctx.mkConst("input_0", ctx.mkIntSort());
    Expr<IntSort> input_1 = ctx.mkConst("input_1", ctx.mkIntSort());
    Simple simple = new Simple();
    Composition composition = new Composition();

    return Stream.of(
      Arguments.of(
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        simple
      ),
      Arguments.of(
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        composition
      )
    );
  }
}