/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/*
 * Invalid model.
 * There are multiple variables with the same name declared in the same scope.
 */
component AmbiguousAJavaVariableNames {

  compute ComputeBlock{
    int x;
    int x;
  }

}
