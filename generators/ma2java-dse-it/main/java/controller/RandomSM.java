/* (c) https://github.com/MontiCore/monticore */
package controller;

import automata.evaluation.smallModel.ListerInSmallModel;
import automata.evaluation.smallModel.ListerParameterSmallModel;
import com.microsoft.z3.CharSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.SeqSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.ListerI;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.InPort;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used to define the randomInput Method for the smallModel
 */
public class RandomSM<In, Out> extends IgnoreSMTSolverController<In, Out> {

  private AnnotatedValue<Expr<IntSort>, Integer> randomInteger() {
    int intValue = new Random().nextInt();
    return AnnotatedValue.newAnnoValue(ctx.mkInt(intValue), intValue);
  }

  private AnnotatedValue<Expr<SeqSort<CharSort>>, String> randomString() {
    String stringValue = RandomStringUtils.random(Math.abs(new Random().nextInt() % 5), true, false);

    return AnnotatedValue.newAnnoValue(ctx.mkString(stringValue), stringValue);
  }

  /**
   * calculates a new random input for the bigModel
   */
  @Override
  protected Pair<List<ListerI>, ListerI> randomInput() {
    List<ListerI> inputList = new ArrayList<>();
    AnnotatedValue<Expr<IntSort>, Integer> parameter = AnnotatedValue.newAnnoValue(ctx.mkInt(359023), 359023);

    ListerI parameterSmallModel = new ListerParameterSmallModel(parameter);

    for (int i = 0; i < inputLength; i++) {
      IInPort<AnnotatedValue<Expr<IntSort>, java.lang.Integer>> mtrNr = new InPort<>();
      mtrNr.update(randomInteger());
      IInPort<AnnotatedValue<Expr<SeqSort<CharSort>>, java.lang.String>> module = new InPort<>();
      module.update(randomString());
      ListerInSmallModel newInput = new ListerInSmallModel(module, mtrNr);
      inputList.add(newInput);
    }

    return ImmutablePair.of(inputList, parameterSmallModel);
  }
}
