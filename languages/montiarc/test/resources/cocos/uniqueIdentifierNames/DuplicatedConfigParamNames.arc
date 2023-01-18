/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedConfigParamNames<VsTypeParam>(int VsConfigParam,
  int VsConfigParam, int VsTypeParam, int VsPort, int VsField, int VsCompType,
  int VsCompInst) {

  port in int VsPort;
  int VsField = 0;

  component VsCompType {}

  Foo VsCompInst;

  component Foo { }
}
