/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Invalid model.
 */
component WithNestedSubCompRefCycle2 {
  component Sub1 s1 {
    component Sub2 s2 {
      component Sub3 s3 {
        Sub1 foo;
      }
    }
  }
}
