package invalid;

component SubcomponentParametersNotCorrectlyAssigned {
 
  component Subcomponent(5) subcomp1;
  component Subcomponent("foo") subcomp2;
}