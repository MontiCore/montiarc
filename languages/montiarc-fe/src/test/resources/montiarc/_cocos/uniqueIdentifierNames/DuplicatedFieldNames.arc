/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedFieldNames<VsTypeParam>(int VsConfigParam) {
  
  port in int VsPort;

  int VsField;
  int VsField;
  int VsTypeParam;
  int VsConfigParam;
  int VsPort;
  int VsCompType;
  int VsCompInst;


  component VsCompType {}

  Foo VsCompInst;

  component Foo {}

}
