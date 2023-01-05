/* (c) https://github.com/MontiCore/monticore */
package mainpackage;

import sympackage.*;
import libpackage.*;

component UsingLibModelsAndImportedSymbols {
  port <<sync>> in BarState inPort;
  port <<sync>> out FooState outPort;

  LibComponent subcomponent;
  LibComponent2 subcomponent2;
}