/* (c) https://github.com/MontiCore/monticore */
package comfy.maxOneAutoconnectPerComponent;

/**
  * Valid model.
  */
component OneOrNoAutoconnect {
  autoconnect off;

  component Alibi alibi {} // To make the model valid

  component Inner1 {
    // No autoconnect
  }

  component Inner2 {
    autoconnect off;
    component NestedAlibi2 alibi {} // To make the model valid
  }

  component Inner3 {
    autoconnect port;
    component NestedAlibi3 alibi {} // To make the model valid
  }

  component Inner4 {
    autoconnect type;
    component NestedAlibi4 alibi {} // To make the model valid
  }
}
