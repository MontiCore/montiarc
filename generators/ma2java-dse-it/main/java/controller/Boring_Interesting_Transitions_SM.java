/* (c) https://github.com/MontiCore/monticore */
package controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to initialize the 'interesting' and 'boring' lists.
 * These attributes cannot be initialized when the controller is initialized.
 * This is because the initialization of the controller is wrapped in the dse tool and is not
 * accessible to the user.
 * An optimization would be to read the stereotypes of the transitions from the MontiArc models and
 * generate a method in the components that assigns the attributes within the tool.
 * <p>
 * Definition of 'interesting' and 'boring' states for the bigModel
 */
public class Boring_Interesting_Transitions_SM<In, Out>
        extends Boring_Interesting_Transitions<In, Out> {

  @Override
  public void init() {
    super.init();

    // define 'boring' transitions
    Set<String> boring = new HashSet<>(Arrays.asList("evaluationFrommdseTononModule2",
            "evaluationFromsaTononModule2",
            "evaluationFromnonModuleTononModule2"));

    // define 'interesting' transitions
    Set<String> interesting = new HashSet<>(Arrays.asList("distinctionFromIdleToIdle1"));

    this.boring = boring;
    this.interesting = interesting;
  }
}
