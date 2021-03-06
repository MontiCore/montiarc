/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.CompWithGenericArg;

/*
 * Invalid model.
 */
component SubCompWithNotExistingTypeAsTypeArg {
  component CompWithGenericArg<NotExistingType>(new NotExistingParmArgType()) subWrong;
  component CompWithGenericArg<String>(new NonExistingTypeInParamArg()) subWrong2;
  component NotExistingComponent<NotExistingType> subWrong3;
}
