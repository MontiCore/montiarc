/* (c) https://github.com/MontiCore/monticore */
package composition;

import Types.SuperType;
import Types.ChildType;

/**
 * Composed component with an input and output port. Its behavior is defined
 * through its sequentially composed subcomponents.
 */
component SubtypeConnection {

  port <<sync>> in ChildType i;
  port <<sync>> out SuperType o;

  /**
   * The component's subcomponents are sequentially composed.
   */
  component SuperTypePassthrough {
    port <<sync>> in SuperType i;
    port <<sync>> out SuperType o;

    compute {
      o = i;
    }
  }

  component ChildTypePassthrough {
    port <<sync>> in ChildType i;
    port <<sync>> out ChildType o;

    compute {
      o = i;
    }
  }

  ChildTypePassthrough c1;
  SuperTypePassthrough c2;

  // Incoming channel
  i -> c1.i;

  // Hidden channel
  c1.o -> c2.i;

  // Outgoing channel
  c2.o -> o;
}
