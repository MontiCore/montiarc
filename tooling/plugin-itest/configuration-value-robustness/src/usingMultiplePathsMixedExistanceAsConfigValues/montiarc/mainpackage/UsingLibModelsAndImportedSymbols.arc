/* (c) https://github.com/MontiCore/monticore */
package mainpackage;

import sympackage.*;
import libpackage.*;

component UsingLibModelsAndImportedSymbols {
  port <<sync>> in int inPort;

  LibComponent subcomponent;
}