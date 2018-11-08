package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericInput;
import components.body.subcomponents._subcomponents.InheritsOutgoingStringPort;

/*
 * Valid model.
 */
component ConnectsIncompatibleInheritedPorts2 extends InheritsOutgoingStringPort {

  component HasGenericInput<String> subComp;
  connect outT -> subComp.inT; // outT is inherited from SuperSuperComp
}
