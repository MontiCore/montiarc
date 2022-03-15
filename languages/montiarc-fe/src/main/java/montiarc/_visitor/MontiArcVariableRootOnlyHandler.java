/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import com.google.common.base.Preconditions;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcVariableRootOnlyHandler implements MontiArcHandler {
  private MontiArcTraverser traverser;

  @Override
  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull MontiArcTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  @Override
  public void traverse(IMontiArcScope node) {
    /* partial copy of generated _visitor.handler.TraverseScope */
    /* keep it up to date with ALL possible symbols from super */
    for (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol s : node.getLocalTypeSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.PortSymbol s : node.getLocalPortSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.ComponentTypeSymbol s : node.getLocalComponentTypeSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (variablearc._symboltable.ArcFeatureSymbol s : node.getLocalArcFeatureSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.oosymbols._symboltable.MethodSymbol s : node.getLocalMethodSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (arcbasis._symboltable.ComponentInstanceSymbol s : node.getLocalComponentInstanceSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol s : node.getLocalOOTypeSymbols()) {
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
    for (de.monticore.scbasis._symboltable.SCStateSymbol s : node.getLocalSCStateSymbols()) {
      if (node.isRootSymbol(s))
        s.accept(getTraverser());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol s : node.getLocalDiagramSymbols()) {
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
