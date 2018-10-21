package components.body.subcomponents;

import components.body.subcomponents._subcomponents.CompWithGenericArg;

/*
 * Invalid model.
 */
component SubCompWithNotExistingTypeAsTypeArg {
  component CompWithGenericArg<NotExistingType>(new NotExistingType()) subWrong;
}