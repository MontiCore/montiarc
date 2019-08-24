/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.AtomicComponent;

/*
 * Valid model.
 *
 * Has component names which could create conflicts in the generation process.
 * This model is therefore used to assert that the conflict resolving is
 * working in the generator.
 */
component HasConflictingSubcomponentNames {

  component AtomicComponent input;
  component AtomicComponent output;
  component AtomicComponent behaviorImpl;
  component AtomicComponent currentState;
}
