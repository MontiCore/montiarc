/* (c) https://github.com/MontiCore/monticore */
package automata;

import assignments.AssignmentLiteral;
import assignments.AssignmentName;
import automata.Datatypes.MotorCmd;
import automata.Datatypes.TimerSignal;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import dataTypes.*;
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

import java.util.ArrayList;
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

  public static Stream<Arguments> histories() {

    Expr<IntSort> input_0 = ctx.mkConst("input_0", ctx.mkIntSort());
    Expr<IntSort> input_1 = ctx.mkConst("input_1", ctx.mkIntSort());

    List<Arguments> result = new ArrayList<>();

    DivExpression div = new DivExpression();
    div.setUp();
    div.init();

    Expr<IntSort> output_0Div = controller.getCtx().mkDiv(input_0, controller.getCtx().mkInt(2));
    AnnotatedValue<Expr<IntSort>, Integer> inDiv = AnnotatedValue.newAnnoValue(input_0, 4);
    AnnotatedValue<Expr<IntSort>, Integer> outDiv = AnnotatedValue.newAnnoValue(output_0Div, 2);

    result.add(Arguments.of(
        List.of(inDiv),
        List.of(outDiv),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          div.getIn().update(input);
          div.compute();
          return div.getOut().getValue();
        },
        List.of(1)
      )
    );

    MinusExpression minus = new MinusExpression();
    minus.setUp();
    minus.init();

    Expr<IntSort> outputMinus = TestController.getCtx()
      .mkSub(input_0, TestController.getCtx().mkInt("1"));
    Expr<IntSort> output_1Minus = TestController.getCtx()
      .mkSub(input_1, TestController.getCtx().mkInt(1));

    AnnotatedValue<Expr<IntSort>, Integer> in_1Minus = AnnotatedValue.newAnnoValue(input_0, 42);
    AnnotatedValue<Expr<IntSort>, Integer> out_1Minus = AnnotatedValue.newAnnoValue(outputMinus,
      41);
    AnnotatedValue<Expr<IntSort>, Integer> in_2Minus = AnnotatedValue.newAnnoValue(input_1, 0);
    AnnotatedValue<Expr<IntSort>, Integer> out_2Minus = AnnotatedValue.newAnnoValue(output_1Minus
      , -1);

    result.add(Arguments.of(
      List.of(in_1Minus),
      List.of(out_1Minus),
      (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
        minus.getIn().update(input);
        minus.compute();
        return minus.getOut().getValue();
      },
      List.of(1)
    ));
    result.add(Arguments.of(
        List.of(in_1Minus, in_2Minus),
        List.of(out_1Minus, out_2Minus),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          minus.getIn().update(input);
          minus.compute();
          return minus.getOut().getValue();
        },
        List.of(1, 1)
      )
    );

    MultExpression mult = new MultExpression();
    mult.setUp();
    mult.init();

    Expr<IntSort> outputMult = TestController.getCtx()
      .mkMul(input_0, TestController.getCtx().mkInt("2"));
    AnnotatedValue<Expr<IntSort>, Integer> inMult = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outMult = AnnotatedValue.newAnnoValue(outputMult, 2);

    result.add(Arguments.of(
        List.of(inMult),
        List.of(outMult),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          mult.getIn().update(input);
          mult.compute();
          return mult.getOut().getValue();
        },
        List.of(1)
      )
    );

    PlusExpression plus = new PlusExpression();
    plus.setUp();
    plus.init();

    Expr<IntSort> outputPlus = TestController.getCtx()
      .mkAdd(input_0, TestController.getCtx().mkInt("1"));
    AnnotatedValue<Expr<IntSort>, Integer> inPlus = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outPlus = AnnotatedValue.newAnnoValue(outputPlus, 2);

    result.add(
      Arguments.of(
        List.of(inPlus),
        List.of(outPlus),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          plus.getIn().update(input);
          plus.compute();
          return plus.getOut().getValue();
        },
        List.of(1)
      )
    );

    Expr<IntSort> outputInternal = TestController.getCtx()
      .mkAdd(TestController.getCtx().mkInt(0), TestController.getCtx().mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> inInternal = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outInternal =
      AnnotatedValue.newAnnoValue(outputInternal, 1);

    InternalVariable internal = new InternalVariable();
    internal.setUp();
    internal.init();

    result.add(
      Arguments.of(
        List.of(inInternal),
        List.of(outInternal),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          internal.getIn().update(input);
          internal.compute();
          return internal.getOut().getValue();
        },
        List.of(1)
      )
    );

    Transitions tran0 = new Transitions();
    tran0.setUp();
    tran0.init();

    Expr<IntSort> outputTran0 = TestController.getCtx().mkInt(100);
    AnnotatedValue<Expr<IntSort>, Integer> inTran0 = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outTran0 = AnnotatedValue.newAnnoValue(outputTran0, 100);

    result.add(

      Arguments.of(
        List.of(inTran0),
        List.of(outTran0),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran0.getIn().update(input);
          tran0.compute();
          return tran0.getOut().getValue();
        },
        List.of(1)
      )

    );
    Transitions tran1 = new Transitions();
    tran1.setUp();
    tran1.init();

    Expr<IntSort> outputTran1 = TestController.getCtx()
      .mkAdd(input_0, TestController.getCtx().mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> inTran1 = AnnotatedValue.newAnnoValue(input_0, 4);
    AnnotatedValue<Expr<IntSort>, Integer> outTran1 = AnnotatedValue.newAnnoValue(outputTran1, 5);

    result.add(
      Arguments.of(
        List.of(inTran1),
        List.of(outTran1),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran1.getIn().update(input);
          tran1.compute();
          return tran1.getOut().getValue();
        },
        List.of(1)
      )
    );

    Transitions tran2 = new Transitions();
    tran2.setUp();
    tran2.init();

    AnnotatedValue<Expr<IntSort>, Integer> inTran2 = AnnotatedValue.newAnnoValue(input_0, 100);

    result.add(
      Arguments.of(
        List.of(inTran2),
        List.of(outTran0),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran2.getIn().update(input);
          tran2.compute();
          return tran2.getOut().getValue();
        },
        List.of(2)
      )
    );
    Transitions tran3 = new Transitions();
    tran3.setUp();
    tran3.init();

    Expr<IntSort> output1Tran3 = ctx.mkAdd(input_0, ctx.mkInt(1));
    Expr<IntSort> output2Tran3 = ctx.mkAdd(input_1, ctx.mkInt(1));
    AnnotatedValue<Expr<IntSort>, Integer> in2Tran3 = AnnotatedValue.newAnnoValue(input_1, 2);
    AnnotatedValue<Expr<IntSort>, Integer> out1Tran3 = AnnotatedValue.newAnnoValue(output1Tran3, 5);
    AnnotatedValue<Expr<IntSort>, Integer> out2Tran3 = AnnotatedValue.newAnnoValue(output2Tran3, 5);

    result.add(
      Arguments.of(
        List.of(inTran1, in2Tran3),
        List.of(out1Tran3, out1Tran3),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran3.getIn().update(input);
          tran3.compute();
          return tran3.getOut().getValue();
        },
        List.of(1, 1)
      )
    );

    Transitions tran4 = new Transitions();
    tran4.setUp();
    tran4.init();

    Expr<IntSort> output2Tran4 = ctx.mkMul(input_1, ctx.mkInt(2));
    AnnotatedValue<Expr<IntSort>, Integer> in2Tran4 = AnnotatedValue.newAnnoValue(input_1, 100);
    AnnotatedValue<Expr<IntSort>, Integer> out2Tran4 = AnnotatedValue.newAnnoValue(output2Tran4,
      200);

    result.add(
      Arguments.of(
        List.of(inTran1, in2Tran4),
        List.of(outTran1, out2Tran4),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          tran4.getIn().update(input);
          tran4.compute();
          return tran4.getOut().getValue();
        },
        List.of(1, 1)
      )
    );

    Composition composition = new Composition();
    composition.setUp();
    composition.init();

    AnnotatedValue<Expr<IntSort>, Integer> inComposition = AnnotatedValue.newAnnoValue(input_0, 42);

    result.add(
      Arguments.of(
        List.of(inComposition),
        List.of(inComposition),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          composition.getIn().update(input);
          composition.compute();
          return composition.getOut().getValue();
        },
        List.of(1)
      )
    );

    AssignmentName name = new AssignmentName();
    name.setUp();
    name.init();

    AnnotatedValue<Expr<IntSort>, Integer> inName = AnnotatedValue.newAnnoValue(input_0, 1);

    result.add(
      Arguments.of(
        List.of(inName),
        List.of(inName),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          name.getIn().update(input);
          name.compute();
          return name.getOut().getValue();
        },
        List.of(1)
      )
    );

    AssignmentLiteral literal = new AssignmentLiteral();
    literal.setUp();
    literal.init();

    AnnotatedValue<Expr<IntSort>, Integer> inLiteral = AnnotatedValue.newAnnoValue(input_0, 1);
    AnnotatedValue<Expr<IntSort>, Integer> outLiteral = AnnotatedValue.newAnnoValue(ctx.mkInt(1),
      1);

    result.add(
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

    Expr<BoolSort> inputB_0 = ctx.mkConst("inputB_0", ctx.mkBoolSort());

    BooleanComponent booleanComponent = new BooleanComponent();
    booleanComponent.setUp();
    booleanComponent.init();

    AnnotatedValue<Expr<BoolSort>, Boolean> inBoolean = AnnotatedValue.newAnnoValue(inputB_0, true);
    AnnotatedValue<Expr<BoolSort>, Boolean> outBoolean =
      AnnotatedValue.newAnnoValue(ctx.mkOr(inputB_0, ctx.mkBool(false)), true);

    AnnotatedValue<Expr<BoolSort>, Boolean> inBoolean2 = AnnotatedValue.newAnnoValue(inputB_0,
      false);
    AnnotatedValue<Expr<BoolSort>, Boolean> outBoolean2 =
      AnnotatedValue.newAnnoValue(ctx.mkOr(inputB_0, ctx.mkBool(false)), false);

    result.add(
      Arguments.of(
        List.of(inBoolean),
        List.of(outBoolean),
        (Function<AnnotatedValue<Expr<BoolSort>, Boolean>, AnnotatedValue<Expr<BoolSort>,
          Boolean>>) (input) -> {
          booleanComponent.getIn().update(input);
          booleanComponent.compute();
          return booleanComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inBoolean2),
        List.of(outBoolean2),
        (Function<AnnotatedValue<Expr<BoolSort>, Boolean>, AnnotatedValue<Expr<BoolSort>,
          Boolean>>) (input) -> {
          booleanComponent.getIn().update(input);
          booleanComponent.compute();
          return booleanComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    Expr<CharSort> inputC_0 = ctx.mkConst("inputC_0", ctx.mkCharSort());

    CharactersComponent charactersComponent = new CharactersComponent();
    charactersComponent.setUp();
    charactersComponent.init();

    AnnotatedValue<Expr<CharSort>, Character> inChar = AnnotatedValue.newAnnoValue(inputC_0, 'c');
    AnnotatedValue<Expr<CharSort>, Character> outChar =
      AnnotatedValue.newAnnoValue(ctx.charFromBv(ctx.mkBV('d', 18)), 'd');

    AnnotatedValue<Expr<CharSort>, Character> inChar2 = AnnotatedValue.newAnnoValue(inputC_0, 'e');
    AnnotatedValue<Expr<CharSort>, Character> outChar2 =
      AnnotatedValue.newAnnoValue(ctx.charFromBv(ctx.mkBV('z', 18)), 'z');

    result.add(
      Arguments.of(
        List.of(inChar),
        List.of(outChar),
        (Function<AnnotatedValue<Expr<CharSort>, Character>, AnnotatedValue<Expr<CharSort>,
          Character>>) (input) -> {
          charactersComponent.getIn().update(input);
          charactersComponent.compute();
          return charactersComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    result.add(

      Arguments.of(
        List.of(inChar2),
        List.of(outChar2),
        (Function<AnnotatedValue<Expr<CharSort>, Character>, AnnotatedValue<Expr<CharSort>,
          Character>>) (input) -> {
          charactersComponent.getIn().update(input);
          charactersComponent.compute();
          return charactersComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    Expr<RealSort> inputR_0 = ctx.mkConst("inputR_0", ctx.mkRealSort());

    DoubleComponent doubleComponent = new DoubleComponent();
    doubleComponent.setUp();
    doubleComponent.init();

    AnnotatedValue<Expr<RealSort>, Double> inDouble = AnnotatedValue.newAnnoValue(inputR_0, 2.1);
    AnnotatedValue<Expr<RealSort>, Double> outDouble =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(inputR_0, ctx.mkReal(String.valueOf(2.4))), 4.5);

    AnnotatedValue<Expr<RealSort>, Double> inDouble2 = AnnotatedValue.newAnnoValue(inputR_0, 4.2);
    AnnotatedValue<Expr<RealSort>, Double> outDouble2 =
      AnnotatedValue.newAnnoValue(ctx.mkReal(String.valueOf(1.0)), 1.0);

    result.add(
      Arguments.of(
        List.of(inDouble),
        List.of(outDouble),
        (Function<AnnotatedValue<Expr<RealSort>, Double>, AnnotatedValue<Expr<RealSort>, Double>>) (input) -> {
          doubleComponent.getIn().update(input);
          doubleComponent.compute();
          return doubleComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inDouble2),
        List.of(outDouble2),
        (Function<AnnotatedValue<Expr<RealSort>, Double>, AnnotatedValue<Expr<RealSort>, Double>>) (input) -> {
          doubleComponent.getIn().update(input);
          doubleComponent.compute();
          return doubleComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    FloatComponent floatComponent = new FloatComponent();
    floatComponent.setUp();
    floatComponent.init();

    AnnotatedValue<Expr<RealSort>, Float> inFloat = AnnotatedValue.newAnnoValue(inputR_0, 6.4f);
    AnnotatedValue<Expr<RealSort>, Float> outFloat =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(inputR_0, ctx.mkReal(String.valueOf(42.4f))),
        48.800003f);

    AnnotatedValue<Expr<RealSort>, Float> inFloat2 = AnnotatedValue.newAnnoValue(inputR_0, 4.2f);
    AnnotatedValue<Expr<RealSort>, Float> outFloat2 =
      AnnotatedValue.newAnnoValue(ctx.mkReal(String.valueOf(1.0f)), 1.0f);

    result.add(
      Arguments.of(
        List.of(inFloat),
        List.of(outFloat),
        (Function<AnnotatedValue<Expr<RealSort>, Float>, AnnotatedValue<Expr<RealSort>, Float>>) (input) -> {
          floatComponent.getIn().update(input);
          floatComponent.compute();
          return floatComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inFloat2),
        List.of(outFloat2),
        (Function<AnnotatedValue<Expr<RealSort>, Float>, AnnotatedValue<Expr<RealSort>, Float>>) (input) -> {
          floatComponent.getIn().update(input);
          floatComponent.compute();
          return floatComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    Expr<SeqSort<CharSort>> inputS_0 = ctx.mkConst("inputS_0", ctx.mkStringSort());

    StringComponent stringComponent = new StringComponent();
    stringComponent.setUp();
    stringComponent.init();

    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inString =
      AnnotatedValue.newAnnoValue(inputS_0, "helloWorld");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> outString =
      AnnotatedValue.newAnnoValue(ctx.mkString("moin"), "moin");

    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inString2 =
      AnnotatedValue.newAnnoValue(inputS_0, "moin");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> outString2 =
      AnnotatedValue.newAnnoValue(ctx.mkString("helloWorld"), "helloWorld");

    result.add(
      Arguments.of(
        List.of(inString),
        List.of(outString),
        (Function<AnnotatedValue<Expr<SeqSort<CharSort>>, String>,
          AnnotatedValue<Expr<SeqSort<CharSort>>, String>>) (input) -> {
          stringComponent.getIn().update(input);
          stringComponent.compute();
          return stringComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inString2),
        List.of(outString2),
        (Function<AnnotatedValue<Expr<SeqSort<CharSort>>, String>,
          AnnotatedValue<Expr<SeqSort<CharSort>>, String>>) (input) -> {
          stringComponent.getIn().update(input);
          stringComponent.compute();
          return stringComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    AnnotatedValue<Expr<IntSort>, Integer> parameterInput =
      AnnotatedValue.newAnnoValue(ctx.mkInt(42), 42);
    Parameter parameter = new Parameter(parameterInput);
    parameter.setUp();
    parameter.init();

    AnnotatedValue<Expr<IntSort>, Integer> inParameter = AnnotatedValue.newAnnoValue(input_0, 1);

    result.add(
      Arguments.of(
        List.of(inParameter),
        List.of(parameterInput),
        (Function<AnnotatedValue<Expr<IntSort>, Integer>, AnnotatedValue<Expr<IntSort>, Integer>>) (input) -> {
          parameter.getIn().update(input);
          parameter.compute();
          return parameter.getOut().getValue();
        },
        List.of(1)
      )
    );

    EnumSort<TimerSignal> timerSignal = ctx.mkEnumSort("TimerSignal", "ALERT", "SLEEP");
    EnumSort<MotorCmd> motorCmd = ctx.mkEnumSort("MotorCmd", "FORWARD", "BACKWARD", "STOP");
    Expr<EnumSort<TimerSignal>> inputE_0 = ctx.mkConst("inputE_0", timerSignal);

    EnumComponent enumComponent = new EnumComponent();
    enumComponent.setUp();
    enumComponent.init();

    AnnotatedValue<Expr<EnumSort<TimerSignal>>, TimerSignal> inEnum =
      AnnotatedValue.newAnnoValue(inputE_0, TimerSignal.ALERT);
    AnnotatedValue<Expr<EnumSort<MotorCmd>>, MotorCmd> outEnum =
      AnnotatedValue.newAnnoValue(motorCmd.getConst(0), MotorCmd.FORWARD);

    AnnotatedValue<Expr<EnumSort<TimerSignal>>, TimerSignal> inEnum2 =
      AnnotatedValue.newAnnoValue(inputE_0, TimerSignal.SLEEP);
    AnnotatedValue<Expr<EnumSort<MotorCmd>>, MotorCmd> outEnum2 =
      AnnotatedValue.newAnnoValue(motorCmd.getConst(2), MotorCmd.STOP);

    result.add(
      Arguments.of(
        List.of(inEnum),
        List.of(outEnum),
        (Function<AnnotatedValue<Expr<EnumSort<TimerSignal>>, TimerSignal>,
          AnnotatedValue<Expr<EnumSort<MotorCmd>>, MotorCmd>>) (input) -> {
          enumComponent.getIn().update(input);
          enumComponent.compute();
          return enumComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inEnum2),
        List.of(outEnum2),
        (Function<AnnotatedValue<Expr<EnumSort<TimerSignal>>, TimerSignal>,
          AnnotatedValue<Expr<EnumSort<MotorCmd>>, MotorCmd>>) (input) -> {
          enumComponent.getIn().update(input);
          enumComponent.compute();
          return enumComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    AnnotatedValue<Expr<IntSort>, Long> parameterLong = AnnotatedValue.newAnnoValue(ctx.mkInt(42)
      , 42l);

    LongComponent longComponent = new LongComponent(parameterLong);
    longComponent.setUp();
    longComponent.init();

    AnnotatedValue<Expr<IntSort>, Long> inLong = AnnotatedValue.newAnnoValue(input_0, 6l);
    AnnotatedValue<Expr<IntSort>, Long> inLong2 = AnnotatedValue.newAnnoValue(input_0, 3l);

    result.add(
      Arguments.of(
        List.of(inLong),
        List.of(inLong),
        (Function<AnnotatedValue<Expr<IntSort>, Long>,
          AnnotatedValue<Expr<IntSort>, Long>>) (input) -> {
          longComponent.getIn().update(input);
          longComponent.compute();
          return longComponent.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(
      Arguments.of(
        List.of(inLong2),
        List.of(parameterLong),
        (Function<AnnotatedValue<Expr<IntSort>, Long>,
          AnnotatedValue<Expr<IntSort>, Long>>) (input) -> {
          longComponent.getIn().update(input);
          longComponent.compute();
          return longComponent.getOut().getValue();
        },
        List.of(1)
      )
    );

    AnnotatedValue<Expr<RealSort>, Double> doubleParameter =
      AnnotatedValue.newAnnoValue(ctx.mkReal("4.2"), 4.2);
    DoubleComponentParameter doubleComponentParameter =
      new DoubleComponentParameter(doubleParameter);

    doubleComponentParameter.setUp();
    doubleComponentParameter.init();

    AnnotatedValue<Expr<RealSort>, Double> inDoubleParmeter =
      AnnotatedValue.newAnnoValue(inputR_0, 6.4);
    AnnotatedValue<Expr<RealSort>, Double> outDoubleParmeter =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(inputR_0, ctx.mkReal("10.2")), 16.6);

    AnnotatedValue<Expr<RealSort>, Double> inDoubleParameter2 =
      AnnotatedValue.newAnnoValue(inputR_0, 2d);

    result.add(Arguments.of(
        List.of(inDoubleParmeter),
        List.of(outDoubleParmeter),
        (Function<AnnotatedValue<Expr<RealSort>, Double>,
          AnnotatedValue<Expr<RealSort>, Double>>) (input) -> {
          doubleComponentParameter.getIn().update(input);
          doubleComponentParameter.compute();
          return doubleComponentParameter.getOut().getValue();
        },
        List.of(1)
      )
    );
    result.add(Arguments.of(
        List.of(inDoubleParameter2),
        List.of(doubleParameter),
        (Function<AnnotatedValue<Expr<RealSort>, Double>,
          AnnotatedValue<Expr<RealSort>, Double>>) (input) -> {
          doubleComponentParameter.getIn().update(input);
          doubleComponentParameter.compute();
          return doubleComponentParameter.getOut().getValue();
        },
        List.of(1)
      )
    );

    return result.stream();
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
      AnnotatedValue<Expr<G>, Z> actOut = component.apply(in.get(i));

      int finalI = i;
      assertAll(
        () -> assertThat(actOut.getValue()).isEqualTo(out.get(finalI).getValue()),
        () -> assertThat(actOut.getExpr().toString()).isEqualTo(out.get(finalI).getExpr()
          .toString())
      );
    }
  }
}