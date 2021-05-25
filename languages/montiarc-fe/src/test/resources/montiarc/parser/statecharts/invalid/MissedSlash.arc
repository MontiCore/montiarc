/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component MissedSlash {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart {
    state Open;
    initial state Closed;
    state Locked;

    // invalid: there is a '/' missing
    Closed -> Open {ringing = true};
  }
}