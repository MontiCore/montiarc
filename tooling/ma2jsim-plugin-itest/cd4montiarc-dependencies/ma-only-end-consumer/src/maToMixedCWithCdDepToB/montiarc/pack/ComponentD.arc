/* (c) https://github.com/MontiCore/monticore */
package pack;

import pack.cda.A;
import pack.cdb.B;
import pack.cdc.C;

component ComponentD {
  port in A aPort,
       in B bPort;

  ComponentC cComp;
  aPort -> cComp.aPort;
  bPort -> cComp.bPort;

  C cVal = C.FOO;
}
