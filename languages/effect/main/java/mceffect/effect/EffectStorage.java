/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import java.util.List;

public interface EffectStorage {
  List<Effect> getEffectsOfComponent(ComponentTypeSymbol component);
}
