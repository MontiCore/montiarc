/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import genericarc.GenericArcMill;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcSymbolTableCompleter;

import java.util.Collection;

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
    this.initVariableArc();
    this.initVarDeclarationStatements();
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

  protected void initVariableArc() {
    VariableArcSymbolTableCompleter variableArcSymbolTableCompleter = VariableArcMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(variableArcSymbolTableCompleter);
    this.getTraverser().add4VariableArc(variableArcSymbolTableCompleter);
    this.getTraverser().setVariableArcHandler(variableArcSymbolTableCompleter);
  }

  protected void initVarDeclarationStatements() {
    MCVarDeclarationStatementsSTCompleteTypes completer =
      new MCVarDeclarationStatementsSTCompleteTypes(new MontiArcTypeCalculator());
    this.getTraverser().add4MCVarDeclarationStatements(completer);
  }

  public void createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }

  public void createFromAST(@NotNull Collection<ASTMACompilationUnit> rootNodes) {
    Preconditions.checkNotNull(rootNodes);
    for (ASTMACompilationUnit rootNode : rootNodes) {
      this.createFromAST(rootNode);
    }
  }
}