/* (c) https://github.com/MontiCore/monticore */
package endconsumerpackage;

import consumerpackage.*;

component TransitiveConsumer {
  port <<sync>> in int incoming;
  port <<sync>> out int outgoing;

  Consumer sub;

  incoming -> sub.incoming;
  sub.outgoing -> outgoing;
}