package components.body.subcomponents;

/**
 * Invalid model. Multiple subcomponent instances of the same name.
 *
 * @implements [Hab16] B1: All names of model elements within a component
 *  namespace have to be unique. (p. 59. Lst. 3.31)
 */
component ComponentInstanceNamesAmbiguous {
 
  component Inner {}
  component Inner inner;
  
  component Inner2 i {}
  component Inner i;
  
}