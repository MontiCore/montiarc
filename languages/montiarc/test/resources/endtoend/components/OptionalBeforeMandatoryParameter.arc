/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: Optional configuration parameters must be declared
 * after all mandatory parameters, but optional parameter p2 is declared
 * before mandatory parameter p1.
 */
component OptionalBeforeMandatoryParameter(int p2 = 1, int p1) { }
