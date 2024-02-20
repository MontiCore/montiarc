/* (c) https://github.com/MontiCore/monticore */
package evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import automata.evaluation.smallModel.ListerInSmallModel;
import automata.evaluation.smallModel.ListerOutSmallModel;
import automata.evaluation.smallModel.SmallModel;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.MockTestController;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import montiarc.rte.timesync.InPort;
import montiarc.rte.timesync.OutPort;
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

public class SmallModelTest {

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

    List<Arguments> result = new ArrayList<>();

    AnnotatedValue<Expr<IntSort>, Integer> parameterSmallModel = AnnotatedValue.newAnnoValue(ctx.mkInt(400000), 400000);

    Expr<IntSort> input_mtrNr1 = ctx.mkConst("input_MtrNr1", ctx.mkIntSort());
    Expr<IntSort> input_mtrNr2 = ctx.mkConst("input_MtrNr2", ctx.mkIntSort());

    Expr<SeqSort<CharSort>> input_module1 = ctx.mkConst("input_Module1", ctx.mkStringSort());
    Expr<SeqSort<CharSort>> input_module2 = ctx.mkConst("input_Module2", ctx.mkStringSort());

    SmallModel smallModel = new SmallModel(parameterSmallModel);
    smallModel.setUp();
    smallModel.init();

    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr1 = AnnotatedValue.newAnnoValue(input_mtrNr1,
      422400);
    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr2 = AnnotatedValue.newAnnoValue(input_mtrNr2,
      422401);

    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule1 =
      AnnotatedValue.newAnnoValue(input_module1, "MDSE");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule2 =
      AnnotatedValue.newAnnoValue(input_module2, "SA");

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr1 = new InPort<>();
    portMtrNr1.update(inMtrNr1);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule1 = new InPort<>();
    portModule1.update(inModule1);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr2 = new InPort<>();
    portMtrNr2.update(inMtrNr2);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule2 = new InPort<>();
    portModule2.update(inModule2);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE1 = AnnotatedValue.newAnnoValue(ctx.mkReal(
      "0.0"), 0.0);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA1 = AnnotatedValue.newAnnoValue(ctx.mkReal("0" +
      ".0"), 0.0);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE2 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(ctx.mkReal("0.0"), ctx.mkReal("1.0")), 1.0);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA2 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(ctx.mkReal("0.0"), ctx.mkReal("0.0")), 0.0);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE1 = new OutPort<>();
    portVoteMDSE1.setValue(outVoteMDSE1);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA1 = new OutPort<>();
    portVoteSA1.setValue(outVoteSA1);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE2 = new OutPort<>();
    portVoteMDSE2.setValue(outVoteMDSE2);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA2 = new OutPort<>();
    portVoteSA2.setValue(outVoteSA2);

    result.add(Arguments.of(
        List.of(new ListerInSmallModel(portModule1, portMtrNr1),
          new ListerInSmallModel(portModule2, portMtrNr2)),
        List.of(new ListerOutSmallModel(portVoteMDSE1, portVoteSA1),
          new ListerOutSmallModel(portVoteMDSE2, portVoteSA2)),
        (Function<ListerInSmallModel, ListerOutSmallModel>) (input) -> {
          smallModel.getMtrNr().update(input.getmtrNr().getValue());
          smallModel.getModule().update(input.getmodule().getValue());
          smallModel.compute();
          ListerOutSmallModel outSmallModel = new ListerOutSmallModel(smallModel.getVoteMDSE(),
            smallModel.getVoteSA());
          smallModel.getComponentCounterSA().getOut().tick();
          smallModel.getComponentCounterMDSE().getOut().tick();
          return outSmallModel;
        },
        List.of(1, 1)
      )
    );

    SmallModel smallModel1 = new SmallModel(parameterSmallModel);
    smallModel1.setUp();
    smallModel1.init();

    Expr<IntSort> input_mtrNr3 = ctx.mkConst("input_MtrNr3", ctx.mkIntSort());
    Expr<IntSort> input_mtrNr4 = ctx.mkConst("input_MtrNr4", ctx.mkIntSort());
    Expr<IntSort> input_mtrNr5 = ctx.mkConst("input_MtrNr5", ctx.mkIntSort());

    Expr<SeqSort<CharSort>> input_module3 = ctx.mkConst("input_Module3", ctx.mkStringSort());
    Expr<SeqSort<CharSort>> input_module4 = ctx.mkConst("input_Module4", ctx.mkStringSort());
    Expr<SeqSort<CharSort>> input_module5 = ctx.mkConst("input_Module5", ctx.mkStringSort());

    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr3 = AnnotatedValue.newAnnoValue(input_mtrNr1,
      350001);
    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr4 = AnnotatedValue.newAnnoValue(input_mtrNr2,
      350002);

    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr5 = AnnotatedValue.newAnnoValue(input_mtrNr3,
      350003);

    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr6 = AnnotatedValue.newAnnoValue(input_mtrNr4,
      350004);
    AnnotatedValue<Expr<IntSort>, Integer> inMtrNr7 = AnnotatedValue.newAnnoValue(input_mtrNr5,
      422401);

    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule3 = AnnotatedValue.newAnnoValue(input_module1, "MDSE");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule4 = AnnotatedValue.newAnnoValue(input_module2, "SA");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule5 = AnnotatedValue.newAnnoValue(input_module3, "MDSE");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule6 = AnnotatedValue.newAnnoValue(input_module4, "mdse");
    AnnotatedValue<Expr<SeqSort<CharSort>>, String> inModule7 = AnnotatedValue.newAnnoValue(input_module5, "SA");

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE3 = AnnotatedValue.newAnnoValue(ctx.mkReal(
      "0.0"), 0.0);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA3 = AnnotatedValue.newAnnoValue(ctx.mkReal("0" +
      ".0"), 0.0);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE4 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(ctx.mkReal("0.0"), ctx.mkReal("1.5")), 1.5);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA4 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(ctx.mkReal("0.0"), ctx.mkReal("0.0")), 0.0);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE5 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteMDSE4.getExpr(), ctx.mkReal("0.0")), 1.5);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA5 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteSA4.getExpr(), ctx.mkReal("1.0")), 1.0);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE6 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteMDSE5.getExpr(), ctx.mkReal("1.0")), 2.5);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA6 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteSA5.getExpr(), ctx.mkReal("0.0")), 1.0);

    AnnotatedValue<Expr<RealSort>, Double> outVoteMDSE7 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteMDSE6.getExpr(), ctx.mkReal("0.0")), 2.5);
    AnnotatedValue<Expr<RealSort>, Double> outVoteSA7 =
      AnnotatedValue.newAnnoValue(ctx.mkAdd(outVoteSA6.getExpr(), ctx.mkReal("0.0")), 1.0);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr3 = new InPort<>();
    portMtrNr3.update(inMtrNr3);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule3 = new InPort<>();
    portModule3.update(inModule3);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr4 = new InPort<>();
    portMtrNr4.update(inMtrNr4);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule4 = new InPort<>();
    portModule4.update(inModule4);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr5 = new InPort<>();
    portMtrNr5.update(inMtrNr5);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule5 = new InPort<>();
    portModule5.update(inModule5);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr6 = new InPort<>();
    portMtrNr6.update(inMtrNr6);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule6 = new InPort<>();
    portModule6.update(inModule6);

    IInPort<AnnotatedValue<Expr<IntSort>, Integer>> portMtrNr7 = new InPort<>();
    portMtrNr7.update(inMtrNr7);

    IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, String>> portModule7 = new InPort<>();
    portModule7.update(inModule7);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE3 = new OutPort<>();
    portVoteMDSE3.setValue(outVoteMDSE3);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA3 = new OutPort<>();
    portVoteSA3.setValue(outVoteSA3);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE4 = new OutPort<>();
    portVoteMDSE4.setValue(outVoteMDSE4);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA4 = new OutPort<>();
    portVoteSA4.setValue(outVoteSA4);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE5 = new OutPort<>();
    portVoteMDSE5.setValue(outVoteMDSE5);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA5 = new OutPort<>();
    portVoteSA5.setValue(outVoteSA5);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE6 = new OutPort<>();
    portVoteMDSE6.setValue(outVoteMDSE6);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA6 = new OutPort<>();
    portVoteSA6.setValue(outVoteSA6);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteMDSE7 = new OutPort<>();
    portVoteMDSE7.setValue(outVoteMDSE7);

    IOutPort<AnnotatedValue<Expr<RealSort>, Double>> portVoteSA7 = new OutPort<>();
    portVoteSA7.setValue(outVoteSA7);

    result.add(
      Arguments.of(
        List.of(new ListerInSmallModel(portModule3, portMtrNr3),
          new ListerInSmallModel(portModule4, portMtrNr4),
          new ListerInSmallModel(portModule5, portMtrNr5),
          new ListerInSmallModel(portModule6,portMtrNr6),
          new ListerInSmallModel(portModule7, portMtrNr7)),
        List.of(new ListerOutSmallModel(portVoteMDSE3, portVoteSA3),
          new ListerOutSmallModel(portVoteMDSE4, portVoteSA4),
          new ListerOutSmallModel(portVoteMDSE5, portVoteSA5),
          new ListerOutSmallModel(portVoteMDSE6, portVoteSA6),
          new ListerOutSmallModel(portVoteMDSE7, portVoteSA7)),
        (Function<ListerInSmallModel, ListerOutSmallModel>) (input) -> {
          smallModel1.getMtrNr().update(input.getmtrNr().getValue());
          smallModel1.getModule().update(input.getmodule().getValue());
          smallModel1.compute();
          ListerOutSmallModel outSmallModel = new ListerOutSmallModel(smallModel1.getVoteMDSE(),
            smallModel1.getVoteSA());
          smallModel1.getComponentCounterMDSE().getOut().tick();
          smallModel1.getComponentCounterSA().getOut().tick();
          return outSmallModel;
        },
        List.of(1, 2, 2, 1, 1)
      )
    );

    return result.stream();
  }

  @ParameterizedTest
  @MethodSource("histories")
  public <T extends Sort, X, G extends Sort, Z> void testCompute(@NotNull List<ListerInSmallModel> in,
                                                                 @NotNull List<ListerOutSmallModel> out,
                                                                 @NotNull Function<ListerInSmallModel, ListerOutSmallModel> component,
                                                                 @NotNull List<Integer> pathControl) {

    Preconditions.checkNotNull(in);
    Preconditions.checkArgument(in.size() > 0);
    Preconditions.checkArgument(out.size() == in.size());

    for (int i = 0; i < in.size(); i++) {
      controller.setTransition(pathControl.get(i));
      ListerOutSmallModel actOut = component.apply(in.get(i));

      int finalI = i;
      assertAll(
        () -> assertThat(actOut.getvoteMDSE().getValue().getValue()).isEqualTo(out.get(finalI)
          .getvoteMDSE().getValue().getValue()),
        () -> assertThat(actOut.getvoteMDSE().getValue().getExpr()
          .toString()).isEqualTo(out.get(finalI).getvoteMDSE().getValue().getExpr().toString()),
        () -> assertThat(actOut.getvoteSA().getValue().getValue()).isEqualTo(out.get(finalI)
          .getvoteSA().getValue().getValue()),
        () -> assertThat(actOut.getvoteSA().getValue().getExpr()
          .toString()).isEqualTo(out.get(finalI).getvoteSA().getValue().getExpr().toString())
      );
    }
  }
}
