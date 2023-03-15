/* (c) https://github.com/MontiCore/monticore */
package comfy.noAutoconnectInAtomicComponents;

/**
  * Valid model.
  */
component NoAutoconnectForComposition {

  component WithNormalInstantiation {
    port in int foo;
    port out int bar;

    Inner1 inner1;
    Inner2 inner2;

    foo -> inner2.foo;
    inner2.bar -> bar;

    component Inner1 { }

    component Inner2 {
      port in int foo;
      port out int bar;
    }
  }

  component WithDirectInstantiation {
      port in int foo;
      port out int bar;

      component Inner1 inner1 { }

      component Inner2 inner2 {
        port in int foo;
        port out int bar;
      }

      foo -> inner2.foo;
      inner2.bar -> bar;
    }
}
