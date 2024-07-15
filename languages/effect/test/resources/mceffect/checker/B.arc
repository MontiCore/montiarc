/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

component B {

  port in boolean i1,
    in boolean i2,
    out boolean o1,
    out boolean o2;

    automaton {
      initial state Anon;
      state Known;

      Anon -> Anon [i1 == true]/{o1 = true;};
      Anon -> Anon [i1 == false]/{o1 = false;};
    }
}