/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.types.OnOff;

component Medium {
  port in OnOff inA, inB;
  port sync in OnOff inY, inZ;

  port out OnOff outA, outB, outY, outZ;

  automaton {
    initial state S;

    S -> S inA / {
      outA = inA;
    }

    S -> S inB / {
      outB = inB;
    }

    S -> S / {
      outY = inY;
      outZ = inZ;
    }
  }
}
