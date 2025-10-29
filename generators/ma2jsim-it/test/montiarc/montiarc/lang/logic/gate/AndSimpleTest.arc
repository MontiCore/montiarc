/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

import montiarc.maunit.api.Assertions;

<<test>>
component AndSimpleTest {
  And sut;
  emitter.a -> sut.a, oracle.a;
  emitter.b -> sut.b, oracle.b;
  sut.q -> oracle.q;

  component Emitter emitter {
    port sync out boolean a;
    port sync out boolean b;

    automaton {
      initial state S;
      S -> S / {
        a = true;
        b = false;
      }
    }
  }

  component Oracle oracle {
    port sync in boolean a, b, q;

    automaton {
      initial state S;
      S -> S / {
        Assertions.assertEquals(a && b, q);
      }
    }
  }
}
