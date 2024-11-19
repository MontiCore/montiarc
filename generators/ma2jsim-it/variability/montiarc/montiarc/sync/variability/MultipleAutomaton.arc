/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

component MultipleAutomaton {
  feature f;
  varif(f){
    port in int i;
    <<sync>> automaton {
      initial state A;
      A -> A [i > 1];
    }
  } else {
    port in String i;
    <<sync>> automaton {
      initial state A;
      A -> A [i == "a"];
    }
  }
}
