/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.SourcePosition;

public class SimpleEffect implements Effect {
  private final ComponentTypeSymbol componentSymbol;
  private final PortSymbol from;
  private final PortSymbol to;

  private final boolean isCheck;

  private final boolean isEnsure;

  private final EffectKind effectKind;

  private final SourcePosition sourcePosition;

  public SimpleEffect(ComponentTypeSymbol componentSymbol, PortSymbol from, PortSymbol to, boolean isCheck, boolean isEnsure, EffectKind effectKind, SourcePosition sourcePosition) {
    this.componentSymbol = componentSymbol;
    this.from = from;
    this.to = to;
    this.isCheck = isCheck;
    this.isEnsure = isEnsure;
    this.effectKind = effectKind;
    this.sourcePosition = sourcePosition;
  }


  @Override
  public ComponentTypeSymbol getComponent() {
    return componentSymbol;
  }

  @Override
  public PortSymbol getFrom() {
    return from;
  }

  @Override
  public PortSymbol getTo() {
    return to;
  }

  @Override
  public boolean isCheck() {
    return isCheck;
  }

  @Override
  public boolean isEnsure() {
    return isEnsure;
  }

  @Override
  public SourcePosition getSourcePosition() {
    return sourcePosition;
  }

  @Override
  public EffectKind getEffectKind() {
    return effectKind;
  }

  @Override
  public String toString() {
    String type = isEnsure ? "ensure" : isCheck ? "check" : "";
    String kind = effectKind.toString().toLowerCase();

    return type + " " + kind + " " + "effect: " + from.getName() + " => " + to.getName() + ";";
  }
}
