/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.sync.automata;

component MultipleAutomaton {
  feature f;
  varif(f){
    port sync in int i;
    automaton {
      initial state A;
      A -> A [i > 1];
    }
  } else {
    port sync in String i;
    automaton {
      initial state A;
      A -> A [i == "a"];
    }
  }
}
