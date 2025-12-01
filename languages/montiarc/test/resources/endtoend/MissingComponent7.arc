/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing of subcomponent sub is missing (the component
 * cannot be resolved). Therefore, the feature sub.f cannot be resolved.
 */
component MissingComponent7 {

  Missing sub;

  constraint(sub.f == true);
}
