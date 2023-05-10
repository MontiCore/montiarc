/* (c) https://github.com/MontiCore/monticore */
package automata;

import assignments.AssignmentLiteral;
import assignments.AssignmentName;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import dse.DSE;
import expressions.DivExpression;
import expressions.MinusExpression;
import expressions.MultExpression;
import expressions.PlusExpression;
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

    assertThat(comp instanceof Empty || comp instanceof InternalVariable || comp instanceof Composition
      || comp instanceof PlusExpression || comp instanceof MultExpression
      || comp instanceof MinusExpression || comp instanceof DivExpression
      || comp instanceof AssignmentLiteral || comp instanceof AssignmentName).isEqualTo(true);

    comp.setUp();
    comp.init();

    if (comp instanceof Empty) ((Empty) comp).getIn().update(in);
    else if (comp instanceof InternalVariable) ((InternalVariable) comp).getIn().update(in);
    else if (comp instanceof Composition) ((Composition) comp).getIn().update(in);
    else if (comp instanceof PlusExpression) ((PlusExpression) comp).getIn().update(in);
    else if (comp instanceof MinusExpression) ((MinusExpression) comp).getIn().update(in);
    else if (comp instanceof MultExpression) ((MultExpression) comp).getIn().update(in);
    else if (comp instanceof DivExpression) ((DivExpression) comp).getIn().update(in);
    else if (comp instanceof AssignmentLiteral) ((AssignmentLiteral) comp).getIn().update(in);
    else if (comp instanceof AssignmentName) ((AssignmentName) comp).getIn().update(in);


    comp.compute();
    if (comp instanceof PlusExpression) {
      assertAll(
        () -> assertThat(((PlusExpression) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((PlusExpression) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((PlusExpression) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((PlusExpression) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof MultExpression) {
      assertAll(
        () -> assertThat(((MultExpression) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((MultExpression) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((MultExpression) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((MultExpression) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof MinusExpression) {
      assertAll(
        () -> assertThat(((MinusExpression) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((MinusExpression) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((MinusExpression) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((MinusExpression) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof DivExpression) {
      assertAll(
        () -> assertThat(((DivExpression) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((DivExpression) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((DivExpression) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((DivExpression) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof AssignmentLiteral) {
      assertAll(
        () -> assertThat(((AssignmentLiteral) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((AssignmentLiteral) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((AssignmentLiteral) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((AssignmentLiteral) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof AssignmentName) {
      assertAll(
        () -> assertThat(((AssignmentName) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((AssignmentName) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((AssignmentName) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((AssignmentName) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
    else if (comp instanceof Empty) {
      assertAll(
        () -> assertThat(((Empty) comp).getIn().getValue().getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((Empty) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((Empty) comp).getOut().getValue()).isEqualTo(out));
    }
    else if (comp instanceof InternalVariable) {
      assertAll(
        () -> assertThat(((InternalVariable) comp).getIn().getValue()
          .getValue()).isEqualTo(in.getValue()),
        () -> assertThat(((InternalVariable) comp).getIn().getValue().getExpr()
          .toString()).isEqualTo(in.getExpr().toString()),
        () -> assertThat(((InternalVariable) comp).getOut().getValue()
          .getValue()).isEqualTo(out.getValue()),
        () -> assertThat(((InternalVariable) comp).getOut().getValue().getExpr()
          .toString()).isEqualTo(out.getExpr().toString())
      );
    }
  }

  public static Stream<Arguments> relations() {

    Expr<IntSort> input = ctx.mkConst("input", ctx.mkIntSort());
    AnnotatedValue<Expr<IntSort>, Integer> in = AnnotatedValue.newAnnoValue(input, 1);
    Empty empty = new Empty();
    Composition composition = new Composition();

    InternalVariable internal = new InternalVariable();
    Expr<IntSort> outputInternal = TestController.getCtx()
      .mkAdd(TestController.getCtx().mkInt(0), TestController.getCtx().mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> outInternal =
      AnnotatedValue.newAnnoValue(outputInternal, 1);

    AssignmentName assignmentName = new AssignmentName();

    AssignmentLiteral assignmentLiteral = new AssignmentLiteral();
    AnnotatedValue<Expr<IntSort>, Integer> outLiteral = AnnotatedValue.newAnnoValue(ctx.mkInt(1),
      1);

    PlusExpression plusExpression = new PlusExpression();
    Expr<IntSort> outputPlus = TestController.getCtx()
      .mkAdd(input, TestController.getCtx().mkInt("1"));
    AnnotatedValue<Expr<IntSort>, Integer> outPlus = AnnotatedValue.newAnnoValue(outputPlus, 2);

    MultExpression multExpression = new MultExpression();
    Expr<IntSort> outputMult = TestController.getCtx()
      .mkMul(input, TestController.getCtx().mkInt("2"));
    AnnotatedValue<Expr<IntSort>, Integer> outMult = AnnotatedValue.newAnnoValue(outputMult, 2);

    MinusExpression minusExpression = new MinusExpression();
    Expr<IntSort> outputMinus = TestController.getCtx()
      .mkSub(input, TestController.getCtx().mkInt("1"));
    AnnotatedValue<Expr<IntSort>, Integer> outMinus = AnnotatedValue.newAnnoValue(outputMinus, 0);

    DivExpression divExpression = new DivExpression();
    AnnotatedValue<Expr<IntSort>, Integer> inDiv = AnnotatedValue.newAnnoValue(input, 4);
    Expr<IntSort> outputDiv = TestController.getCtx()
      .mkDiv(input, TestController.getCtx().mkInt("2"));
    AnnotatedValue<Expr<IntSort>, Integer> outDiv = AnnotatedValue.newAnnoValue(outputDiv, 2);

    return Stream.of(
      Arguments.of(in, null, empty),
      Arguments.of(in, outInternal, internal),
      Arguments.of(in, in, composition),
      Arguments.of(in, in, assignmentName),
      Arguments.of(in, outLiteral, assignmentLiteral),
      Arguments.of(in, outPlus, plusExpression),
      Arguments.of(in, outMult, multExpression),
      Arguments.of(in, outMinus, minusExpression),
      Arguments.of(inDiv, outDiv, divExpression)
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

    assertThat(comp instanceof Empty || comp instanceof InternalVariable || comp instanceof Composition
      || comp instanceof PlusExpression || comp instanceof MultExpression
      || comp instanceof MinusExpression || comp instanceof DivExpression).isEqualTo(true);

    comp.setUp();
    comp.init();

    Integer[] actOut = new Integer[out.length];
    Expr<IntSort>[] actExprOut = new Expr[out.length];

    if (comp instanceof PlusExpression) {
      for (int j = 0; j < in.length; j++) {
        ((PlusExpression) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));

        comp.compute();

        actOut[j] = ((PlusExpression) comp).getOut().getValue().getValue();
        actExprOut[j] = ((PlusExpression) comp).getOut().getValue().getExpr();
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
    else if (comp instanceof MultExpression) {
      for (int j = 0; j < in.length; j++) {
        ((MultExpression) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));

        comp.compute();
        actOut[j] = ((MultExpression) comp).getOut().getValue().getValue();
        actExprOut[j] = ((MultExpression) comp).getOut().getValue().getExpr();
      }
    }
    else if (comp instanceof DivExpression) {
      for (int j = 0; j < in.length; j++) {
        ((DivExpression) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));
        comp.compute();
        actOut[j] = ((DivExpression) comp).getOut().getValue().getValue();
        actExprOut[j] = ((DivExpression) comp).getOut().getValue().getExpr();
      }
    }
    else if (comp instanceof MinusExpression) {
      for (int j = 0; j < in.length; j++) {
        ((MinusExpression) comp).getIn().update(AnnotatedValue.newAnnoValue(exprIn[j], in[j]));
        comp.compute();
        actOut[j] = ((MinusExpression) comp).getOut().getValue().getValue();
        actExprOut[j] = ((MinusExpression) comp).getOut().getValue().getExpr();
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

    Composition composition = new Composition();

    PlusExpression plus = new PlusExpression();
    Expr<IntSort> output_0Plus = TestController.getCtx()
      .mkAdd(input_0, TestController.getCtx().mkInt(1));
    Expr<IntSort> output_1Plus = TestController.getCtx()
      .mkAdd(input_1, TestController.getCtx().mkInt(1));

    MinusExpression minusExpression = new MinusExpression();
    Expr<IntSort> output_0Minus = TestController.getCtx()
      .mkSub(input_0, TestController.getCtx().mkInt(1));
    Expr<IntSort> output_1Minus = TestController.getCtx()
      .mkSub(input_1, TestController.getCtx().mkInt(1));

    DivExpression divExpression = new DivExpression();
    Expr<IntSort> output_0Div = TestController.getCtx()
      .mkDiv(input_0, TestController.getCtx().mkInt(2));
    Expr<IntSort> output_1Div = TestController.getCtx()
      .mkDiv(input_1, TestController.getCtx().mkInt(2));

    MultExpression multExpression = new MultExpression();
    Expr<IntSort> output_0Mult = TestController.getCtx()
      .mkMul(input_0, TestController.getCtx().mkInt(2));
    Expr<IntSort> output_1Mult = TestController.getCtx()
      .mkMul(input_1, TestController.getCtx().mkInt(2));

    return Stream.of(
      Arguments.of(
        new Integer[]{42, 10},
        new Expr[]{input_0, input_1},
        new Integer[]{43, 11},
        new Expr[]{output_0Plus, output_1Plus},
        plus
      ),
      Arguments.of(new Integer[]{42, 0},
        new Expr[]{input_0, input_1},
        new Integer[]{41, -1},
        new Expr[]{output_0Minus, output_1Minus},
        minusExpression
      ),
      Arguments.of(
        new Integer[]{2, 10},
        new Expr[]{input_0, input_1},
        new Integer[]{1, 5},
        new Expr[]{output_0Div, output_1Div},
        divExpression
      ),
      Arguments.of(
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        new Integer[]{42, 42},
        new Expr[]{input_0, input_1},
        composition
      ),
      Arguments.of(
        new Integer[]{2, 5},
        new Expr[]{input_0, input_1},
        new Integer[]{4, 10},
        new Expr[]{output_0Mult, output_1Mult},
        multExpression
      )
    );
  }
}
