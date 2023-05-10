/* (c) https://github.com/MontiCore/monticore */
package dse;

/* generated by template ma2java.dse.Import-dse.ftl*/

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;

/* generated by template ma2java.dse.Component-dse.ftl*/

/* generated by template ma2java.component.Header.ftl*/

public class AssignmentName implements montiarc.rte.timesync.IComponent {

  private String instanceName = "";

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getInstanceName() {
    return this.instanceName;
  }

  // ports
  /* generated by template ma2java.dse.Port-dse.ftl*/

  protected montiarc.rte.timesync.IInPort<
          montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
      in;

  public montiarc.rte.timesync.IInPort<
          montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
      getIn() {
    return this.in;
  }

  public void setIn(
      montiarc.rte.timesync.IInPort<
              montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
          in) {
    this.in = in;
  }

  /* generated by template ma2java.dse.Port-dse.ftl*/

  protected montiarc.rte.timesync.IOutPort<
          montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
      out;

  public montiarc.rte.timesync.IOutPort<
          montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
      getOut() {
    return this.out;
  }

  public void setOut(
      montiarc.rte.timesync.IOutPort<
              montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer>>
          out) {
    this.out = out;
  }

  // parameters

  // variables

  public AssignmentName() {

    // Context for Solver
    Context ctx = montiarc.rte.dse.TestController.getCtx();
  }

  /* generated by template ma2java.component.Atomic.ftl*/

  /* generated by template ma2java.dse.Automaton-dse.ftl*/

  protected States currentState;

  protected States getCurrentState() {
    return this.currentState;
  }

  public void compute() {
    montiarc.rte.log.Log.comment("Computing component " + this.getInstanceName() + "");
    // log state @ pre
    montiarc.rte.log.Log.trace("State@pre = " + this.getCurrentState());
    // log input values
    montiarc.rte.log.Log.trace("Value of input port in = " + this.getIn().getValue().getValue());
    montiarc.rte.log.Log.trace(
        "Symbolic Expression of input port in = " + this.getIn().getValue().getExpr());

    // transition from the current state
    switch (currentState) {
      case Idle:
        transitionFromIdle();
        break;
    }
    // log output values
    montiarc.rte.log.Log.trace("Value of output port out = " + this.getOut().getValue().getValue());
    montiarc.rte.log.Log.trace(
        "Symbolic Expression of output port out = " + this.getOut().getValue().getExpr());
    // log state @ post
    montiarc.rte.log.Log.trace("State@post = " + this.getCurrentState());
  }

  protected enum States {
    Idle();

    final States superState;

    java.util.Optional<States> getSuperState() {
      return java.util.Optional.ofNullable(this.superState);
    }

    States() {
      this.superState = null;
    }

    States(States superState) {
      this.superState = superState;
    }
  }

  protected void exit(States from, States to) {
    switch (from) {
      case Idle:
        exitIdle();
        break;
    }
    if (from != to && from.getSuperState().isPresent()) {
      exit(from.getSuperState().get(), to);
    }
  }

  protected void transitionFromIdle() {

    // Context for Solver
    Context ctx = montiarc.rte.dse.TestController.getCtx();

    // input
    final montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer> in =
        this.getIn().getValue();
    /* generated by template ma2java.dse.Transition-dse.ftl*/

    // exit state(s)
    this.exit(this.getCurrentState(), States.Idle);
    // output
    montiarc.rte.dse.AnnotatedValue<Expr<IntSort>, Integer> out = null;

    // reaction
    {
      out = montiarc.rte.dse.AnnotatedValue.newAnnoValue(in.getExpr(), in.getValue());
      montiarc.rte.log.Log.trace("out" + " Expr: " + out.getExpr() + "Value: " + out.getValue());
    }
    // result
    if (out != null) this.getOut().setValue(out);

    // entry state(s)
    this.transitionToIdle();

    this.getOut().sync();
  }

  protected void transitionToIdle() {
    // transition to state
    this.currentState = States.Idle;
    this.entryIdle();
  }

  protected void entryIdle() {}

  protected void exitIdle() {}

  protected void initIdle() {}

  @Override
  public void init() {
    // execute the initial action
    this.initIdle();
    // transition to the initial state
    this.transitionToIdle();
    // provide initial value for delay ports

  }

  public void setUp() {
    this.in =
        new montiarc.rte.timesync.InPort<>(
            !this.getInstanceName().isBlank() ? this.getInstanceName() + "." + "in" : "in");
    this.out =
        new montiarc.rte.timesync.OutPort<>(
            !this.getInstanceName().isBlank() ? this.getInstanceName() + "." + "out" : "out");
  }

  @Override
  public void tick() {
    // update outgoing ports
    this.out.tick();
  }

  @Override
  public boolean isSynced() {
    return this.getIn().isSynced();
  }
}
