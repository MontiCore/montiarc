/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.ComponentWithConfigParameter;

/*
 * Valid model.
 */
component ComponentWithConfigArgPassingToSub(UUID uuid) {

  component ComponentWithConfigParameter(uuid);

}