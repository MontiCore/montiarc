/* (c) https://github.com/MontiCore/monticore */
package controller;

import automata.evaluation.bigModel.ListerInElevatorSystem;
import automata.evaluation.bigModel.ListerParameterElevatorSystem;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.ListerI;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.InPort;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used to define the randomInput Method for the bigModel
 */
public class RandomBM<In, Out> extends IgnoreSMTSolverController<In, Out> {

  /**
   * calculates a new random input for the bigModel
   */
  @Override
  protected Pair<List<ListerI>, ListerI> randomInput() {
    List<ListerI> inputList = new ArrayList<>();
    AnnotatedValue<Expr<IntSort>, Integer> parameter = AnnotatedValue.newAnnoValue(ctx.mkInt(3), 3);

    ListerI parameterElevatorSystem = new ListerParameterElevatorSystem(parameter);

    for (int i = 0; i < inputLength; i++) {
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> btn1 = new InPort<>();
      btn1.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> btn2 = new InPort<>();
      btn2.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> btn3 = new InPort<>();
      btn3.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> btn4 = new InPort<>();
      btn4.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> at1 = new InPort<>();
      at1.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> at2 = new InPort<>();
      at2.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> at3 = new InPort<>();
      at3.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> at4 = new InPort<>();
      at4.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> isOpen = new InPort<>();
      isOpen.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> isClosed = new InPort<>();
      isClosed.update(randomBool());
      IInPort<AnnotatedValue<Expr<BoolSort>, Boolean>> isObstacle = new InPort<>();
      isObstacle.update(randomBool());

      ListerInElevatorSystem newInput
        = new ListerInElevatorSystem(btn1, btn2, btn3, btn4, at1, at2, at3, at4,
        isOpen, isClosed, isObstacle);
      inputList.add(newInput);
    }

    return ImmutablePair.of(inputList, parameterElevatorSystem);
  }

  private AnnotatedValue<Expr<BoolSort>, Boolean> randomBool() {
    boolean boolValue = new Random().nextBoolean();
    return AnnotatedValue.newAnnoValue(ctx.mkBool(boolValue), boolValue);
  }
}
