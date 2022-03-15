/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

public class VariableArcRootOnlyHandler implements VariableArcHandler {

  private VariableArcTraverser traverser;

  @Override
  public VariableArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull VariableArcTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  @Override
  public void traverse(IVariableArcScope node) {
    /* partial copy of generated _visitor.handler.TraverseScope*/
    for (de.monticore.symbols.oosymbols._symboltable.MethodSymbol s : node.getLocalMethodSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.PortSymbol s : node.getLocalPortSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.ComponentInstanceSymbol s : node.getLocalComponentInstanceSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol s : node.getLocalTypeSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol s : node.getLocalOOTypeSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.ComponentTypeSymbol s : node.getLocalComponentTypeSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol s : node.getLocalFunctionSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.VariableSymbol s : node.getLocalVariableSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol s : node.getLocalDiagramSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (variablearc._symboltable.ArcFeatureSymbol s : node.getLocalArcFeatureSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol s : node.getLocalTypeVarSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.oosymbols._symboltable.FieldSymbol s : node.getLocalFieldSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
  }
}
