/* (c) https://github.com/MontiCore/monticore */
package dse;

import automata.Simple;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Model;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import montiarc.rte.timesync.InPort;
import montiarc.rte.timesync.OutPort;

import java.util.ArrayList;
import java.util.List;

/*
 * Example dse_main file, will be generated later, currently needed for testing purposes
 */
public class DSE {

  public static List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> getInputValues(Model model,
                                                                                     List<Expr<IntSort>> inputs) {
    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> result = new ArrayList<>();
    for (Expr<IntSort> expr : inputs) {

      IInPort<AnnotatedValue<Expr<IntSort>, Integer>> in = new InPort<>();
      in.update(AnnotatedValue.newAnnoValue(expr, Integer.parseInt(model.eval(expr, true)
        .toString())));
      result.add(in);
    }
    return result;
  }

  public static List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>> runOnce(
    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> input) {

    Simple sum = new Simple();
    sum.setUp();
    sum.init();
    sum.setInstanceName("Comp_sum");

    List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>> result = new ArrayList<>();

    for (IInPort<AnnotatedValue<Expr<IntSort>, Integer>> in : input) {
      sum.getIn().update(in.getValue());

      sum.compute();

      IOutPort<AnnotatedValue<Expr<IntSort>, Integer>> out = new OutPort<>();

      out.setValue(AnnotatedValue.newAnnoValue(sum.getOut().getValue().getExpr(), sum.getOut()
        .getValue().getValue()));

      result.add(out);

    }
    return result;
  }
}
