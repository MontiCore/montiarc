/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

component Nor {

  port in boolean a,
       in boolean b;
  port out boolean q;

  boolean aLast = false;
  boolean bLast = false;

  automaton {
    initial state None;
    state A;
    state B;

    None -> A a / {
      aLast = a;
    }
    A -> A a / {
      aLast = a;
    }
    A -> None b / {
      q = !(aLast || b);
    }

    None -> B b / {
      bLast = b;
    }
    B -> B b / {
      bLast = b;
    }
    B -> None a / {
      q = !(a || bLast);
    }
  }
}
