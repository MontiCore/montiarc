/* (c) https://github.com/MontiCore/monticore */
package pack;

import pack.cda.A;
import pack.cdb.B;

component ComponentD {
  autoconnect port;
  port in A aPort,
       in B bPort;

  ComponentC cComp;
}
