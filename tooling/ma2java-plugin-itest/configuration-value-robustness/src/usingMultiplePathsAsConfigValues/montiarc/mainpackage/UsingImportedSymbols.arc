/* (c) https://github.com/MontiCore/monticore */
package mainpackage;

import sympackage.*;
import libpackage.*;

component UsingImportedSymbols {
  port <<sync>> in BarState inPort;

  LibComponent subcomponent;
  LibComponent2 subcomponent2;
}