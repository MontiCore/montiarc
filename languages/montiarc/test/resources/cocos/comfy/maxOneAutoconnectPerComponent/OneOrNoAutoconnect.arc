/* (c) https://github.com/MontiCore/monticore */
package comfy.maxOneAutoconnectPerComponent;

/**
  * Valid model.
  */
component OneOrNoAutoconnect {
  autoconnect off;

  component Inner1 {
    // No autoconnect
  }

  component Inner2 {
    autoconnect off;
  }

  component Inner3 {
    autoconnect port;
  }

  component Inner4 {
    autoconnect type;
  }
}
