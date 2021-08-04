/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcSymbolTableCompleterDelegator {

  protected IComfortableArcGlobalScope globalScope;

  protected ComfortableArcTraverser traverser;

  protected ComfortableArcTraverser getTraverser() {
    return this.traverser;
  }

  public ComfortableArcSymbolTableCompleterDelegator() {
    this.globalScope = ComfortableArcMill.globalScope();
    this.traverser = ComfortableArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}