/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * The component's behavior is defined by an hierarchical automaton.
 */
component Hierarchy {
  port <<sync>> out String oPath;
  String path = "";

  automaton {
    initial {
      path = path + "aIni";
    } state A {
      entry / {
        path = path + "->aEn";
        oPath = path;
      }
      exit / {
        path = path + "->aEx";
      }
      initial {
          path = path + "->a1Ini";
        } state A1 {
          entry / {
            path = path + "->a1En";
            oPath = path;
          }
          exit / {
            path = path + "->a1Ex";
          }
      };
    };
    state B {
      entry / {
        path = path + "->bEn";
        oPath = path;
      }
      exit / {
        path = path + "->bEx";
      }
    };
    state C {
      entry / {
        path = path + "->cEn";
        oPath = path;
      }
      exit / {
        path = path + "->cEx";
      }
      state C1 {
        entry / {
          path = path + "->c1En";
          oPath = path;
        }
        exit / {
          path = path + "->c1Ex";
        }
      };
      state C2 {
        entry / {
          path = path + "->c2En";
          oPath = path;
        }
        exit / {
          path = path + "->c2Ex";
        }
      };
    };
    state D {
      entry / {
        path = path + "->dEn";
        oPath = path;
      }
      exit / {
        path = path + "->dEx";
      }
      state D1 {
        entry / {
          path = path + "->d1En";
          oPath = path;
        }
        exit / {
          path = path + "->d1Ex";
        }
      };
      initial state D2 {
        entry / {
          path = path + "->d2En";
          oPath = path;
        }
        exit / {
          path = path + "->d2Ex";
        }
      };
    };
    state E {
      entry / {
        path = path + "->eEn";
        oPath = path;
      }
      exit / {
        path = path + "->eEx";
      }
      initial {
        path = path + "->e1Ini";
      } state E1 {
        entry / {
          path = path + "->e1En";
          oPath = path;
        }
        exit / {
          path = path + "->e1Ex";
        }
      };
      initial { // This should not be called coming from E1
        path = path + "->e2Ini";
      } state E2 {
        entry / {
          path = path + "->e2En";
          oPath = path;
        }
        exit / {
          path = path + "->e2Ex";
        }
      };
    };
    state F {
      entry / {
        path = path + "->fEn";
        oPath = path;
      }
      exit / {
        path = path + "->fEx";
      }
      initial state F1 {
        entry / {
          path = path + "->f1En";
          oPath = path;
        }
        exit / {
          path = path + "->f1Ex";
        }
        initial { // This should not be called coming from E
          path = path + "->f11Ini";
        } state F11 {
          entry / {
            path = path + "->f11En";
            oPath = path;
          }
          exit / {
            path = path + "->f11Ex";
          }
        };
      };
    };

    A -> B / {
      path = path + "->aToB";
    };
    B -> C / {
      path = path + "->bToC";
    };
    C1 -> C2 / {
      path = path + "->c1ToC2";
    };
    C -> D / {
      path = path + "->cToD";
    };
    D2 -> D1 / {
      path = path + "->d2ToD1";
    };
    D1 -> E / {
      path = path + "->d1ToE";
    };
    E1 -> E2 / {
      path = path + "->e1ToE2";
    };
    E -> F11 / {
      path = path + "->eToF11";
    };
    F -> A / {
      path = path + "->fToA";
    };
  }
}