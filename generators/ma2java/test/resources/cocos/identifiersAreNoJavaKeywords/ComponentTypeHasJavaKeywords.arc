/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component ComponentTypeHasJavaKeywords {
  //component abstract {}   // Already leads to a parse error
  component assert {}
  //component boolean {}  // Parse error
  //component break {}    // Parse error
  //component byte {}     // Parse error
  //component case {}     // Parse error
  component catch {}
  //component char {}     // Parse error
  component class {}
  //component const {}    // Parse Error

  component continue {}
  //component default {}  // Parse error
  //component do {}       // Parse error
  //component double {}   // Parse error
  //component else {}     // Parse error
  component enum {}
  //component extends {}  // Parse error
  //component final {}    // Parse error
  component finally {}
  //component float {}      // Parse error

  //component for {}        // Parse error
  component goto {}
  //component if {}        // Parse error
  component implements {}
  //component import {}       // Parse error
  component instanceof {}
  //component component {}          // Parse error
  component interface {}
  //component long {}         // Parse error
  //component native {}       // Parse error

  component new {}
  //component package {}        // Parse error
  //component private {}        // Parse error
  //component protected {}      // Parse error
  //component public {}         // Parse error
  component return {}
  //component short {}          // Parse error
  //component static {}         // Parse error
  //component strictfp {}       // Parse error
  component super {}

  //component switch {}         // Parse error
  //component synchronized {}   // Parse error
  component this {}
  component throw {}
  component throws {}
  //component transient {}      // Parse error
  component try {}
  //component void {}           // Parse error
  //component volatile {}       // Parse error
  //component while {}          // Parse error

}