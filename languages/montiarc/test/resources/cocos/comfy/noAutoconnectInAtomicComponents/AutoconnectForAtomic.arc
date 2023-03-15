/* (c) https://github.com/MontiCore/monticore */
package comfy.noAutoconnectInAtomicComponents;

/**
  * Invalid model.
  */
component AutoconnectForAtomic {

  component Inner1 {
    autoconnect off;
  }

  component Inner2 {
    autoconnect port;
  }

  component Inner3 {
    autoconnect type;
  }
}
