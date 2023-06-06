/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcautomaton.ArcAutomatonMill;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import genericarc.GenericArcMill;
import genericarc._symboltable.GenericArcScopesGenitorP2;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcScopesGenitorP2;

import java.util.Collection;

public class MontiArcScopesGenitorP2Delegator {

  protected IMontiArcGlobalScope globalScope;

  protected MontiArcTraverser traverser;

  protected MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcScopesGenitorP2Delegator() {
    this.globalScope = MontiArcMill.globalScope();
    this.traverser = MontiArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
    this.initArcAutomaton();
    this.initGenericArc();
    this.initVariableArc();
    this.initVarDeclarationStatements();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 scopesGenP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(scopesGenP2);
    this.getTraverser().setArcBasisHandler(scopesGenP2);
  }
  
  protected void initArcAutomaton() {
    this.getTraverser().add4ArcAutomaton(ArcAutomatonMill.scopesGenitorP2());
  }

  protected void initGenericArc() {
    GenericArcScopesGenitorP2 scopesGenP2 = GenericArcMill.scopesGenitorP2();
    this.getTraverser().add4GenericArc(scopesGenP2);
    this.getTraverser().setGenericArcHandler(scopesGenP2);
  }

  protected void initVariableArc() {
    VariableArcScopesGenitorP2 scopesGenP2 = VariableArcMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(scopesGenP2);
    this.getTraverser().add4VariableArc(scopesGenP2);
    this.getTraverser().setVariableArcHandler(scopesGenP2);
  }

  protected void initVarDeclarationStatements() {
    MCVarDeclarationStatementsSTCompleteTypes scopesGenP2 =
      new MCVarDeclarationStatementsSTCompleteTypes(new MontiArcTypeCalculator());
    this.getTraverser().add4MCVarDeclarationStatements(scopesGenP2);
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