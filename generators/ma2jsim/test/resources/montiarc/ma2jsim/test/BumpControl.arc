/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component BumpControl {
  port
    sync out int right,
    sync out int left;

  automaton {
    initial state Idle {
      entry / {
        right = 1;
        left = 2;
      }
    }

    state Driving;
    state Backing;
    state Turning;

    Idle -> Driving / {
      right = 0;
      left = 3;
    }
  }
}
