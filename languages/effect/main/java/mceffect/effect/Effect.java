/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.SourcePosition;

public interface Effect {
  ComponentTypeSymbol getComponent();

  PortSymbol getFrom();

  PortSymbol getTo();

  boolean isCheck();

  boolean isEnsure();

  SourcePosition getSourcePosition();

  EffectKind getEffectKind();

  String toString();
}
