/* (c) https://github.com/MontiCore/monticore */
package automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Atomic component whose behavior is defined by an hierarchical automaton.
 */
component Hierarchy {
  port out int pCounter;
  port out List<String> pPath;
  int counter = 0;
  // List<String> path = ArrayList();

  automaton {
    initial {
      // path.add("aIni");
      pCounter = counter;
      counter++;
    } state A {
      entry / {
        // path.add("aEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("aEx");
        pCounter = counter;
        counter++;
      }
    };
    state B {
      entry / {
        // path.add("bEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("bEx");
        pCounter = counter;
        counter++;
      }
    };
    state C {
      entry / {
        // path.add("cEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("cEx");
        pCounter = counter;
        counter++;
      }
      state C1 {
        entry / {
          // path.add("c1En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("c1Ex");
          pCounter = counter;
          counter++;
        }
      };
      state C2 {
        entry / {
          // path.add("c2En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("c2Ex");
          pCounter = counter;
          counter++;
        }
      };
    };
    state D {
      entry / {
        // path.add("dEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("dEx");
        pCounter = counter;
        counter++;
      }
      state D1 {
        entry / {
          // path.add("d1En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("d1Ex");
          pCounter = counter;
          counter++;
        }
      };
      initial state D2 {
        entry / {
          // path.add("d2En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("d2Ex");
          pCounter = counter;
          counter++;
        }
      };
    };
    state E {
      entry / {
        // path.add("eEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("eEx");
        pCounter = counter;
        counter++;
      }
      initial state E1 {
        entry / {
          // path.add("e1En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("e1Ex");
          pCounter = counter;
          counter++;
        }
      };
      initial state E2 {
        entry / {
          // path.add("e2En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("e2Ex");
          pCounter = counter;
          counter++;
        }
      };
    };
    state F {
      entry / {
        // path.add("fEn");
        pCounter = counter;
        counter++;
      }
      exit / {
        // path.add("fEx");
        pCounter = counter;
        counter++;
      }
      initial state F1 {
        entry / {
          // path.add("f1En");
          pCounter = counter;
          counter++;
        }
        exit / {
          // path.add("f1Ex");
          pCounter = counter;
          counter++;
        }
        initial state F11 {
          entry / {
            // path.add("f11En");
            pCounter = counter;
            counter++;
          }
          exit / {
            // path.add("f11Ex");
            pCounter = counter;
            counter++;
          }
        };
      };
    };

    A -> B / {
      // path.add("aToB");
      pCounter = counter;
      counter++;
    };
    B -> C;
    C1 -> C2;
    C -> D;
    D2 -> D1;
    D1 -> E;
    E -> F11 / {
      // pPath = path;
    };
  }
}