package components.body.subcomponents;

/**
 * Invalid model. Multiple subcomponent instances of the same name.
 */
component ComponentInstanceNamesAmbiguous {
 
  component Inner {}
  component Inner inner;
  
  component Inner2 i {}
  component Inner i;
  
}