/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.ComponentWithConfigParameter;


component ComponentWithConfigArgPassingToSub(UUID uuid) {

  component ComponentWithConfigParameter(uuid);

}
