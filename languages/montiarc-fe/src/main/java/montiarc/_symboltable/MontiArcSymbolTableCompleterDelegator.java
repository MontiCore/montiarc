/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import genericarc.GenericArcMill;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcSymbolTableCompleterDelegator {

  protected IMontiArcGlobalScope globalScope;

  protected MontiArcTraverser traverser;

  protected MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcSymbolTableCompleterDelegator() {
    this.globalScope = MontiArcMill.globalScope();
    this.traverser = MontiArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
    this.initGenericArc();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  protected void initGenericArc() {
    GenericArcSymbolTableCompleter genericArcSymbolTableCompleter = GenericArcMill.symbolTableCompleter();
    this.getTraverser().add4GenericArc(genericArcSymbolTableCompleter);
    this.getTraverser().setGenericArcHandler(genericArcSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}