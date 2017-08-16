package invalid;

component ComponentInstanceNamesNotUnique {
 
  component Inner {}
  component Inner inner;
  
  component Inner2 i {}
  component Inner i;
  
}