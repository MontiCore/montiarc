package components.body.ports;

/**
 * Invalid model. Muliple connectors send to rootOut.
 * @implements [Hab16] R1: Each outgoing port of a component type definition is used at most once as target of
 * a connector. (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at most once as target of a connector. (p. 62, Lst. 3.37)
 */
component InPortAmbiguousSender {
  port out String rootOut;

  component A {
    port out String aOut;
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.aOut -> b.bIn;
  
  connect a.aOut -> rootOut;
  
  component A myA
    [aOut -> b.bIn, rootOut];
}