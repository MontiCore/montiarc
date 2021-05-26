/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// valid
component UsedParameter(int parameter) {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [parameter == 7];
  }
}