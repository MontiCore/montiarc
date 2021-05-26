/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedPortNames<VsTypeParam>(int VsConfigParam) {
  
  port in int VsPort;
  port in int VsPort;
  port in int VsTypeParam;
  port in int VsConfigParam;
  port in int VsField;
  port in int VsCompType;
  port in int VsCompInst;

  int VsField;

  component VsCompType {}

  Foo VsCompInst;

  component Foo {}

}
