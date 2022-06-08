/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component MissedSlashButGuard {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    state Open;
    initial state Closed;
    state Locked;

    // invalid: there is a '/' missing
    Closed -> Open [unlock] {ringing = true};
  }
}