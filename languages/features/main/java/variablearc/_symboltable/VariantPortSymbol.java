/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;

import java.util.Optional;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariantPortSymbol extends PortSymbol {

  protected VariantComponentTypeSymbol variantComponentTypeSymbol;

  public VariantPortSymbol(PortSymbol parent, VariantComponentTypeSymbol variantSymbol) {
    super(parent.getName());
    this.setIncoming(parent.isIncoming());
    this.setOutgoing(parent.isOutgoing());
    this.setType(parent.getType());
    this.setEnclosingScope(parent.getEnclosingScope());
    this.setAccessModifier(parent.getAccessModifier());
    if (parent.isPresentAstNode()) {
      this.setAstNode(parent.getAstNode());
    }

    if (!variantSymbol.isPresentAstNode()) { // workaround for not being able to recalculate the timings without the AST
      this.setTiming(parent.getTiming());
      this.setDelayed(parent.getDelayed());
      this.setStronglyCausal(parent.getStronglyCausal());
    }

    variantComponentTypeSymbol = variantSymbol;
  }

  @Override
  public Optional<ComponentTypeSymbol> getComponent() {
    return Optional.of(variantComponentTypeSymbol);
  }
}
