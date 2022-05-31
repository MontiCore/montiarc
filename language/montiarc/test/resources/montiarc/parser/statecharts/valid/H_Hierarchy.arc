/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * Valid atomic component whose behavior is defined by an hierarchical automaton.
 */
component H_Hierarchy {
  port out int pCounter;
  int counter = 0;

  automaton {
    initial {
      pCounter = counter;
      counter++;
    } state A {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
    };
    state B {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
    };
    state C {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
      state C1 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };
      state C2 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };

      C1 -> C2;
    };
    state D {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
      state D1 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };
      initial state D2 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };
    };
    state E {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
      initial state E1 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };
      initial state E2 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
      };
    };
    state F {
      entry / {
        pCounter = counter;
        counter++;
      }
      exit / {
        pCounter = counter;
        counter++;
      }
      initial state F1 {
        entry / {
          pCounter = counter;
          counter++;
        }
        exit / {
          pCounter = counter;
          counter++;
        }
        initial state F11 {
          entry / {
            pCounter = counter;
            counter++;
          }
          exit / {
            pCounter = counter;
            counter++;
          }
        };
      };
    };

    A -> B / {
      pCounter = counter;
      counter++;
    };
    B -> C;
    C -> D;
    D2 -> D1;
    D1 -> E;
    E -> F11;
  }
}