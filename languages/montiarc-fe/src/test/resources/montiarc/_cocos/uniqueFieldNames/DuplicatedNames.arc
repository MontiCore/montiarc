/* (c) https://github.com/MontiCore/monticore */
package uniqueFieldNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedNames {

  int a = 1;
  int b = 2;
  String a = "foo";
  String b = "bar";
}