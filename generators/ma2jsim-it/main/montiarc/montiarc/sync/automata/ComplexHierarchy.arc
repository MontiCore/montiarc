/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

/**
 * The component's behavior is defined by an hierarchical automaton.
 */
component ComplexHierarchy {
  port out String o;
  String path = "";

  <<sync>> automaton {
    initial {
      path = path + "aIni";
      o = "aIni";
    } state A {
      entry / {
        path = path + "->aEn";
        o = "aEn";
      }
      exit / {
        path = path + "->aEx";
        o = "aEx";
      }
      initial {
          path = path + "->a1Ini";
          o = "a1Ini";
        } state A1 {
          entry / {
            path = path + "->a1En";
            o = "a1En";
          }
          exit / {
            path = path + "->a1Ex";
            o = "a1Ex";
          }
      };
    };
    state B {
      entry / {
        path = path + "->bEn";
        o = "bEn";
      }
      exit / {
        path = path + "->bEx";
        o = "bEx";
      }
    };
    state C {
      entry / {
        path = path + "->cEn";
        o = "cEn";
      }
      exit / {
        path = path + "->cEx";
        o = "cEx";
      }
      state C1 {
        entry / {
          path = path + "->c1En";
          o = "c1En";
        }
        exit / {
          path = path + "->c1Ex";
          o = "c1Ex";
        }
      };
      state C2 {
        entry / {
          path = path + "->c2En";
          o = "c2En";
        }
        exit / {
          path = path + "->c2Ex";
          o = "c2Ex";
        }
      };
    };
    state D {
      entry / {
        path = path + "->dEn";
        o = "dEn";
      }
      exit / {
        path = path + "->dEx";
        o = "dEx";
      }
      state D1 {
        entry / {
          path = path + "->d1En";
          o = "d1En";
        }
        exit / {
          path = path + "->d1Ex";
          o = "d1Ex";
        }
      };
      initial state D2 {
        entry / {
          path = path + "->d2En";
          o = "d2En";
        }
        exit / {
          path = path + "->d2Ex";
          o = "d2Ex";
        }
      };
    };
    state E {
      entry / {
        path = path + "->eEn";
        o = "eEn";
      }
      exit / {
        path = path + "->eEx";
        o = "eEx";
      }
      initial {
        path = path + "->e1Ini";
        o = "e1Ini";
      } state E1 {
        entry / {
          path = path + "->e1En";
          o = "e1En";
        }
        exit / {
          path = path + "->e1Ex";
          o = "e1Ex";
        }
      };
      initial { // This should not be called coming from E1
        path = path + "->e2Ini";
        o = "e2Ini";
      } state E2 {
        entry / {
          path = path + "->e2En";
          o = "e2En";
        }
        exit / {
          path = path + "->e2Ex";
          o = "e2Ex";
        }
      };
    };
    state F {
      entry / {
        path = path + "->fEn";
        o = "fEn";
      }
      exit / {
        path = path + "->fEx";
        o = "fEx";
      }
      initial state F1 {
        entry / {
          path = path + "->f1En";
          o = "f1En";
        }
        exit / {
          path = path + "->f1Ex";
          o = "f1Ex";
        }
        initial { // This should not be called coming from E
          path = path + "->f11Ini";
          o = "f11Ini";
        } state F11 {
          entry / {
            path = path + "->f11En";
            o = "f11En";
          }
          exit / {
            path = path + "->f11Ex";
            o = "f11Ex";
          }
        };
      };
    };

    A -> B / {
      path = path + "->aToB";
      o = "aToB";
    };
    B -> C / {
      path = path + "->bToC";
      o = "bToC";
    };
    C1 -> C2 / {
      path = path + "->c1ToC2";
      o = "c1ToC2";
    };
    C -> D / {
      path = path + "->cToD";
      o = "cToD";
    };
    D2 -> D1 / {
      path = path + "->d2ToD1";
      o = "d2ToD1";
    };
    D1 -> E / {
      path = path + "->d1ToE";
      o = "d1ToE";
    };
    E1 -> E2 / {
      path = path + "->e1ToE2";
      o = "e1ToE2";
    };
    E -> F11 / {
      path = path + "->eToF11";
      o = "eToF11";
    };
    F -> A / {
      path = path + "->fToA";
      o = "fToA";
    };
  }
}
