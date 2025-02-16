/* (c) https://github.com/MontiCore/monticore */
package checker;

component B {

  port in boolean i1,
    in boolean i2,
    out boolean o1,
    out boolean o2;

  automaton {
    initial state Anon;
    state Known;

    Anon -> Anon [i1 == true] i1 / { o1 = true; };
    Anon -> Anon [i1 == false] i1 / { o1 = false; };
  }
}
