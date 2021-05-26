/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedTypeParamNames
  <VsTypeParam, VsTypeParam, VsConfigParam, VsField, VsPort, VsCompType, VsCompInst>
  (int VsConfigParam) {

  port in int VsPort;
  int VsField;

  component VsCompType {}

  Foo VsCompInst;

  component Foo {}
}