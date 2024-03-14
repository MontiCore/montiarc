/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;

import java.util.Optional;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariantPortSymbol extends ArcPortSymbol {

  protected ArcPortSymbol parent;

  protected ComponentTypeSymbol component;

  public VariantPortSymbol(ArcPortSymbol parent, ComponentTypeSymbol component) {
    super(parent.getName());
    this.parent = parent;
    this.setIncoming(parent.isIncoming());
    this.setOutgoing(parent.isOutgoing());
    this.setType(parent.getType());
    this.setEnclosingScope(parent.getEnclosingScope());
    this.setAccessModifier(parent.getAccessModifier());
    if (parent.isPresentAstNode()) {
      this.setAstNode(parent.getAstNode());
    }

    if (!parent.isPresentAstNode()) { // workaround for not being able to recalculate the timings without the AST
      this.setTiming(parent.getTiming());
      this.setDelayed(parent.getDelayed());
      this.setStronglyCausal(parent.getStronglyCausal());
    }

    this.component = component;
  }

  public ArcPortSymbol getOriginal() {
    return parent;
  }

  @Override
  public Optional<ComponentTypeSymbol> getComponent() {
    return Optional.of(component);
  }
}
