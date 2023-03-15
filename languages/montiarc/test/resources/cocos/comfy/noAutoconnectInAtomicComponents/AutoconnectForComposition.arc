/* (c) https://github.com/MontiCore/monticore */
package comfy.noAutoconnectInAtomicComponents;

/**
  * Valid model.
  */
component AutoconnectForComposition {

  component DirectInstantiation1 {
    autoconnect off;
    component Nested1 nested {}
  }

  component DirectInstantiation2 {
    autoconnect port;
    component Nested2 nested {}
  }

  component DirectInstantiation3 {
    autoconnect type;
    component Nested3 nested {}
  }

    component NormalInstantiation1 {
      autoconnect off;

      component Nested4 {}
      Nested4 nested;
    }

    component NormalInstantiation2 {
      autoconnect port;

      component Nested5 {}
      Nested5 nested;
    }

    component NormalInstantiation3 {
      autoconnect type;

      component Nested6 {}
      Nested6 nested;
    }
}
