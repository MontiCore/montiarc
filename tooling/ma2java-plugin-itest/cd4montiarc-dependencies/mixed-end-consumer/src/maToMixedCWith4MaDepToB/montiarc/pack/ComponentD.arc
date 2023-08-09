/* (c) https://github.com/MontiCore/monticore */
package pack;

import pack.cda.A;
import pack.cdb.B;
import pack.cdc.C;

component ComponentD {
  autoconnect port;
  port in A aPort,
       in B bPort;

  ComponentC cComp;

  C cVal = C.FOO;
}
