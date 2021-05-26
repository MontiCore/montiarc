/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedSubCompNames<VsTypeParam>(int VsConfigParam) {
  port in int VsPort;
  int VsField;

  component VsCompType {}

  Foo VsCompInst;
  Foo VsCompInst;
  Foo VsTypeParam;
  Foo VsConfigParam;
  Foo VsPort;
  Foo VsField;
  Foo VsCompType;

  component Foo {}

}