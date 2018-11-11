package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericOutput;
import components.body.subcomponents._subcomponents.InheritsOutgoingStringPort;

/*
 * Valid model.
 */
component ConnectsIncompatibleInheritedPorts2 extends InheritsOutgoingStringPort {

  component HasGenericOutput<String> subComp;
  connect subComp.outT -> outT; // outT is inherited from SuperSuperComp
}
