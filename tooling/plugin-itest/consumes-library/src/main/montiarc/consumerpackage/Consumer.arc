/* (c) https://github.com/MontiCore/monticore */
package consumerpackage;

import libpackage.*;

component Consumer {
  port <<sync>> in int incoming;
  port <<sync>> out int outgoing;

  LibComponent sub;

  incoming -> sub.incoming;
  sub.outgoing -> outgoing;
}