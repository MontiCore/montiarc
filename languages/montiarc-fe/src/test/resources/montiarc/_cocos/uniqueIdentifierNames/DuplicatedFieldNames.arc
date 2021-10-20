/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedFieldNames<VsTypeParam>(int VsConfigParam) {

  port in int VsPort;

  int VsField = 0;
  int VsField = 0;
  int VsTypeParam = 0;
  int VsConfigParam = 0;
  int VsPort = 0;
  int VsCompType = 0;
  int VsCompInst = 0;

  component VsCompType { }

  Foo VsCompInst;

  component Foo { }
}