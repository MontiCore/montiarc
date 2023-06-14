/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

public class InputAndCondition<In, Out> {

  private final In input;

  private final Out output;

  private final PathCondition branches;

  public InputAndCondition(In in, Out out, PathCondition expr) {
    this.input = in;
    this.output = out;
    this.branches = expr;
  }

  public static <In, Out> InputAndCondition<In, Out> newInputCondition(In in, Out out,
                                                                       PathCondition branches) {
    return new InputAndCondition<>(in, out, branches);
  }

  public In getInput() {
    return this.input;
  }

  public Out getOutput() {
    return this.output;
  }

  public PathCondition getBranches() {
    return this.branches;
  }

  @Override
  public String toString() {
    return "[<" + input.toString() + ", " + output.toString() + ">, " + branches.getBranchConditions()
      .toString() + "]";
  }
}