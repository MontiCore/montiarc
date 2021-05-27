/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Invalid model. Is used in other components provoking subcomponent reference cycles.
 */
component DummyWithNestedCycle {
  component Sup1 s1{
    component Sup2 s2{
      component Sup3 s3{
        WithNestedSubCompRefCycle withCyc;
      }
    }
  }
}