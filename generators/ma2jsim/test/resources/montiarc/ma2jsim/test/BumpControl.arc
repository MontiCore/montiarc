/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component BumpControl {
  port
    out int right,
    out int left;

  <<sync>> automaton {
    initial {
      right = 1;
      left = 2;
    } state Idle;

    state Driving;
    state Backing;
    state Turning;

    Idle -> Driving / {
      right = 0;
      left = 3;
    };
  }
}
