/* (c) https://github.com/MontiCore/monticore */
package automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Atomic component whose behavior is defined by an hierarchical automaton.
 */
component Hierarchy {
  port out int pCounter;
  port out String pPath;
  int counter = 0;
  String path = "";

  automaton {
    initial {
      path = path + "aIni";
      pCounter = counter;
      counter++;
    } state A {
      entry / {
        path = path + "aEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "aEx";
        pCounter = counter;
        counter++;
      }
    };
    state B {
      entry / {
        path = path + "bEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "bEx";
        pCounter = counter;
        counter++;
      }
    };
    state C {
      entry / {
        path = path + "cEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "cEx";
        pCounter = counter;
        counter++;
      }
      state C1 {
        entry / {
          path = path + "c1En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "c1Ex";
          pCounter = counter;
          counter++;
        }
      };
      state C2 {
        entry / {
          path = path + "c2En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "c2Ex";
          pCounter = counter;
          counter++;
        }
      };
    };
    state D {
      entry / {
        path = path + "dEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "dEx";
        pCounter = counter;
        counter++;
      }
      state D1 {
        entry / {
          path = path + "d1En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "d1Ex";
          pCounter = counter;
          counter++;
        }
      };
      initial state D2 {
        entry / {
          path = path + "d2En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "d2Ex";
          pCounter = counter;
          counter++;
        }
      };
    };
    state E {
      entry / {
        path = path + "eEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "eEx";
        pCounter = counter;
        counter++;
      }
      initial state E1 {
        entry / {
          path = path + "e1En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "e1Ex";
          pCounter = counter;
          counter++;
        }
      };
      initial state E2 {
        entry / {
          path = path + "e2En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "e2Ex";
          pCounter = counter;
          counter++;
        }
      };
    };
    state F {
      entry / {
        path = path + "fEn";
        pCounter = counter;
        counter++;
      }
      exit / {
        path = path + "fEx";
        pCounter = counter;
        counter++;
      }
      initial state F1 {
        entry / {
          path = path + "f1En";
          pCounter = counter;
          counter++;
        }
        exit / {
          path = path + "f1Ex";
          pCounter = counter;
          counter++;
        }
        initial state F11 {
          entry / {
            path = path + "f11En";
            pCounter = counter;
            counter++;
            pPath = path;
          }
          exit / {
            path = path + "f11Ex";
            pCounter = counter;
            counter++;
          }
        };
      };
    };

    A -> B / {
      path = path + "aToB";
      pCounter = counter;
      counter++;
    };
    B -> C;
    C1 -> C2;
    C -> D;
    D2 -> D1;
    D1 -> E;
    E -> F11;
  }
}