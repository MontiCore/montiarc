/* (c) https://github.com/MontiCore/monticore */
package comfy.maxOneAutoconnectPerComponent;

/**
  * Valid model.
  */
component MultipleAutoconnects {
  autoconnect off;
  autoconnect off;

  component Inner1 {
    autoconnect off;
    autoconnect off;
  }

  component Inner2 {
    autoconnect off;
    autoconnect port;
  }

  component Inner3 {
    autoconnect off;
    autoconnect type;
  }

  component Inner4 {
    autoconnect port;
    autoconnect off;
  }

  component Inner5 {
    autoconnect port;
    autoconnect port;
  }

  component Inner6 {
    autoconnect port;
    autoconnect type;
  }

  component Inner7 {
    autoconnect type;
    autoconnect off;
  }

  component Inner8 {
    autoconnect type;
    autoconnect port;
  }

  component Inner9 {
    autoconnect type;
    autoconnect type;
  }

  component Inner10 {
    autoconnect off;
    autoconnect port;
    autoconnect type;
  }
}