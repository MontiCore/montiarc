/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component ConfusedTransition {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart {
    state Open;
    initial state Closed;
    state Locked;

    // invalid: guard is defined in the wrong position
    Closed -> [unlock] Open / {ringing = true};
  }
}