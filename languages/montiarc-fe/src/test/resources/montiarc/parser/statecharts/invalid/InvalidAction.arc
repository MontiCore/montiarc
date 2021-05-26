/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component InvalidAction {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    state Open;
    initial state Closed;
    state Locked {
      entry / {ringing = true;}
      fish / {ringing = false;}
    };
    // invalid: there is no fish-action
  }
}