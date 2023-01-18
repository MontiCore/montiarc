/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component DeclareStatesWithoutStatechart {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  // invalid, because this has to be wrapped in a "automaton"-block
  state Open;
  initial state Closed;
  state Locked;
}
