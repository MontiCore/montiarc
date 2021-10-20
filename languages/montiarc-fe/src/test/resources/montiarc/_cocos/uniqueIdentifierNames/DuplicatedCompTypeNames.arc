/* (c) https://github.com/MontiCore/monticore */
package uniqueIdentifierNames;

/**
 * Invalid model because of redundant names.
 */
component DuplicatedCompTypeNames<VsTypeParam>(int VsConfigParam) {

  port in int VsPort;
  int VsField = 0;

  component VsCompType {}
  component VsCompType {}
  component VsTypeParam {}
  component VsConfigParam {}
  component VsPort {}
  component VsField {}
  component VsCompInst {}

  Foo VsCompInst;

  component Foo { }
}