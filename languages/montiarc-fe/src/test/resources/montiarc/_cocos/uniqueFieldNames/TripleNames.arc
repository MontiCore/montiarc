/* (c) https://github.com/MontiCore/monticore */
package uniqueFieldNames;

/**
 * Invalid model because of redundant names.
 */
component TripleNames {

  int a = 1, a = 2, a = 3;
}