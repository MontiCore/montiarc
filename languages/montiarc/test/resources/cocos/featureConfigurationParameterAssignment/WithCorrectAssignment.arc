/* (c) https://github.com/MontiCore/monticore */

import subcomponentDefinitions.*;

/**
 * Valid model.
 */
component WithCorrectAssignment {
  Simple simple(true, f1=false);
  Simple simple(true, f1=true);
  Complex c1(true, f2=true&&false);
  Complex c2(true, 1 + 1, f3=true, f2=false);
  Complex c3(true, 2, 3.2 * 2.3, f3=true, f1=true||false);
}