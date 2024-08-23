/* (c) https://github.com/MontiCore/monticore */
package pack;

import pack.cda.A;
import pack.cdb.B;
import pack.cdc.C;

component ComponentC {
  port in A aPort,
       in B bPort;

  C cVal = C.FOO;
}
