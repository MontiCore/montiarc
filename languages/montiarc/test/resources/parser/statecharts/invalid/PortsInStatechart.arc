/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component PortsInStatechart {

  port out boolean ringing;

  automaton {
    state Open;
    initial state Closed;
    state Locked;

    // invalid, because the ports should be defined outside the "automaton"-block
    port in boolean open,
         in boolean unlock;
  }
}
