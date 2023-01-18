/* (c) https://github.com/MontiCore/monticore */
package rootComponentTypesNoInstanceName;

/**
 * Valid model.
 */
component RootWithoutInstanceName {
  component InnerComp1 {}
  component InnerComp2 legalInstantiationName2 {}
  component InnerComp3 legalInstantiationName3_1, legalInstantiationName3_2 {}
}
