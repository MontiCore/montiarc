/* (c) https://github.com/MontiCore/monticore */
package automata;

import assignments.AssignmentLiteral;
import assignments.AssignmentName;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Sort;
import expressions.DivExpression;
import expressions.MinusExpression;
import expressions.MultExpression;
import expressions.PlusExpression;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.MockTestController;
import montiarc.rte.dse.TestController;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ComponentsTest {

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

  @ParameterizedTest
  @MethodSource("histories")
  public <T extends Sort, X, G extends Sort, Z> void testCompute(@NotNull List<AnnotatedValue<Expr<T>, X>> in,
                                                                 @NotNull List<AnnotatedValue<Expr<G>, Z>> out,
                                                                 @NotNull Function<AnnotatedValue<Expr<T>, X>, AnnotatedValue<Expr<G>, Z>> component,
                                                                 @NotNull List<Integer> pathControl) {

    Preconditions.checkNotNull(in);
    Preconditions.checkArgument(in.size() > 0);
    Preconditions.checkArgument(out.size() == in.size());

    for (int i = 0; i < in.size(); i++) {
      controller.setTransition(pathControl.get(i));
      AnnotatedValue<Expr<G>, Z> actOut = component.apply(in.get(0));

      assertAll(
        () -> assertThat(actOut.getValue()).isEqualTo(out.get(0).getValue()),
        () -> assertThat(actOut.getExpr().toString()).isEqualTo(out.get(0).getExpr().toString())
      );
    }
  }

  public static Stream<Arguments> histories() {

    Expr<IntSort> input_0 = ctx.mkConst("input_0", ctx.mkIntSort());
    Expr<IntSort> input_1 = ctx.mkConst("input_1", ctx.mkIntSort());

    Expr<IntSort> output_0Div = controller.getCtx().mkDiv(input_0, controller.getCtx().mkInt(2));
    AnnotatedValue<Expr<IntSort>, Integer> inDiv = AnnotatedValue.newAnnoValue(input_0, 4);
    AnnotatedValue<Expr<IntSort>, Integer> outDiv = AnnotatedValue.newAnnoValue(output_0Div, 2);

    DivExpression div = new DivExpression();
    div.setUp();
    div.init();

    Expr<IntSort> outputMinus = TestController.getCtx()
      .mkSub(input_0, TestController.getCtx().mkInt("1"));
    Expr<IntSort> output_1Minus = TestController.getCtx()
      .mkSub(input_1, TestController.getCtx().mkInt(1));

    AnnotatedValue<Expr<IntSort>, Integer> in_1Minus = AnnotatedValue.newAnnoValue(input_0, 42);
    AnnotatedValue<Expr<IntSort>, Integer> out_1Minus = AnnotatedValue.newAnnoValue(outputMinus,
      41);
    AnnotatedValue<Expr<IntSort>, Integer> in_2Minus = AnnotatedValue.newAnnoValue(input_0, 0);
    AnnotatedValue<Expr<IntSort>, Integer> out_2Minus = AnnotatedValue.newAnnoValue(output_1Minus
      , -1);

    MinusExpression minus = new MinusExpression();
    minus.setUp();
    minus.init();

    Expr<IntSort> outputMult = TestController.getCtx()
      .mkMul(input_0, TestController.getCtx().mkInt("2"));
    AnnotatedValue<Expr<IntSort>, Integer> inMult = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outMult = AnnotatedValue.newAnnoValue(outputMult, 2);

    MultExpression mult = new MultExpression();
    mult.setUp();
    mult.init();

    Expr<IntSort> outputPlus = TestController.getCtx()
      .mkAdd(input_0, TestController.getCtx().mkInt("1"));
    AnnotatedValue<Expr<IntSort>, Integer> inPlus = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outPlus = AnnotatedValue.newAnnoValue(outputPlus, 2);

    PlusExpression plus = new PlusExpression();
    plus.setUp();
    plus.init();

    Expr<IntSort> outputInternal = TestController.getCtx()
      .mkAdd(TestController.getCtx().mkInt(0), TestController.getCtx().mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> inInternal = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outInternal =
      AnnotatedValue.newAnnoValue(outputInternal, 1);

    InternalVariable internal = new InternalVariable();
    internal.setUp();
    internal.init();

    Transitions tran0 = new Transitions();
    tran0.setUp();
    tran0.init();

    Expr<IntSort> outputTran0 = TestController.getCtx().mkInt(100);
    AnnotatedValue<Expr<IntSort>, Integer> inTran0 = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outTran0 = AnnotatedValue.newAnnoValue(outputTran0, 100);

    Transitions tran1 = new Transitions();
    tran1.setUp();
    tran1.init();

    Expr<IntSort> outputTran1 = TestController.getCtx()
      .mkAdd(input_0, TestController.getCtx().mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> inTran1 = AnnotatedValue.newAnnoValue(input_0, 4);
    AnnotatedValue<Expr<IntSort>, Integer> outTran1 = AnnotatedValue.newAnnoValue(outputTran1, 5);

    Transitions tran2 = new Transitions();
    tran2.setUp();
    tran2.init();

    AnnotatedValue<Expr<IntSort>, Integer> inTran2 = AnnotatedValue.newAnnoValue(input_0, 100);

    Transitions tran3 = new Transitions();
    tran3.setUp();
    tran3.init();

    Expr<IntSort> output1Tran3 = ctx.mkAdd(input_0, ctx.mkInt(1));
    Expr<IntSort> output2Tran3 = ctx.mkAdd(input_1, ctx.mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> in2Tran3 = AnnotatedValue.newAnnoValue(input_1, 2);
    AnnotatedValue<Expr<IntSort>, Integer> out1Tran3 = AnnotatedValue.newAnnoValue(output1Tran3, 5);
    AnnotatedValue<Expr<IntSort>, Integer> out2Tran3 = AnnotatedValue.newAnnoValue(output2Tran3, 5);

    Transitions tran4 = new Transitions();
    tran4.setUp();
    tran4.init();

    Expr<IntSort> output2Tran4 = ctx.mkMul(input_1, ctx.mkInt(2));
    AnnotatedValue<Expr<IntSort>, Integer> in2Tran4 = AnnotatedValue.newAnnoValue(input_1, 100);
    AnnotatedValue<Expr<IntSort>, Integer> out2Tran4 = AnnotatedValue.newAnnoValue(output2Tran4,
      200);

    Composition composition = new Composition();
    composition.setUp();
    composition.init();

    AnnotatedValue<Expr<IntSort>, Integer> inComposition = AnnotatedValue.newAnnoValue(input_0, 42);

    AssignmentName name = new AssignmentName();
    name.setUp();
    name.init();

    AnnotatedValue<Expr<IntSort>, Integer> inName = AnnotatedValue.newAnnoValue(input_0, 1);

    AssignmentLiteral literal = new AssignmentLiteral();
    literal.setUp();
    literal.init();

    AnnotatedValue<Expr<IntSort>, Integer> inLiteral = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outLiteral = AnnotatedValue.newAnnoValue(ctx.mkInt(1),
      1);

    return Stream.of(

      Arguments.of(
        List.of(inDiv),
        List.of(outDiv),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          div.getIn().update(input);
          div.compute();
          return div.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(in_1Minus),
        List.of(out_1Minus),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          minus.getIn().update(input);
          minus.compute();
          return minus.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(in_1Minus, in_2Minus),
        List.of(out_1Minus, out_2Minus),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          minus.getIn().update(input);
          minus.compute();
          return minus.getOut().getValue();
        },
        List.of(1, 1)
      ),
      Arguments.of(
        List.of(inMult),
        List.of(outMult),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          mult.getIn().update(input);
          mult.compute();
          return mult.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inPlus),
        List.of(outPlus),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          plus.getIn().update(input);
          plus.compute();
          return plus.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inInternal),
        List.of(outInternal),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          internal.getIn().update(input);
          internal.compute();
          return internal.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inTran0),
        List.of(outTran0),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran0.getIn().update(input);
          tran0.compute();
          return tran0.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inTran1),
        List.of(outTran1),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran1.getIn().update(input);
          tran1.compute();
          return tran1.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inTran2),
        List.of(outTran0),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran2.getIn().update(input);
          tran2.compute();
          return tran2.getOut().getValue();
        },
        List.of(2)
      ),
      Arguments.of(
        List.of(inTran1, in2Tran3),
        List.of(out1Tran3, out2Tran3),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran3.getIn().update(input);
          tran3.compute();
          return tran3.getOut().getValue();
        },
        List.of(1, 1)
      ),
      Arguments.of(
        List.of(inTran1, in2Tran4),
        List.of(outTran1, out2Tran4),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran4.getIn().update(input);
          tran4.compute();
          return tran4.getOut().getValue();
        },
        List.of(1, 1)
      ),
      Arguments.of(
        List.of(inComposition),
        List.of(inComposition),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          composition.getIn().update(input);
          composition.compute();
          return composition.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inName),
        List.of(inName),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          name.getIn().update(input);
          name.compute();
          return name.getOut().getValue();
        },
        List.of(1)
      ),
      Arguments.of(
        List.of(inLiteral),
        List.of(outLiteral),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          literal.getIn().update(input);
          literal.compute();
          return literal.getOut().getValue();
        },
        List.of(1)
      )
    );
  }
}
