/* (c) https://github.com/MontiCore/monticore */
package testing.structure;

/**
 * provides a permanent stream of 1,1,1,1,...
 */
component Ones {

  port out int outputValue;

  compute {
    outputValue = 1;
  }
}