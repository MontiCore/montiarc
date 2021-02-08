/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MontiArcScopesGenitor extends MontiArcScopesGenitorTOP {

  public MontiArcScopesGenitor() {
    super(new MontiArcGlobalScope());
  }

  public MontiArcScopesGenitor(@NotNull IMontiArcScope enclosingScope) {
    super(enclosingScope);
  }

  IMontiArcScope watchscope;

  public MontiArcScopesGenitor(
    @NotNull Deque<? extends IMontiArcScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public IMontiArcArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    IMontiArcArtifactScope artifactScope = MontiArcMill.artifactScope();
    artifactScope.setPackageName(rootNode.isPresentPackage() ? rootNode.getPackage().getQName() : "");
    artifactScope.setImportsList(imports);
    putOnStack(artifactScope);
    watchscope = artifactScope;
    setLinkBetweenSpannedScopeAndNode(artifactScope, rootNode);
    rootNode.accept(getTraverser());
    return artifactScope;
  }

  @Override
  public void visit(ASTMACompilationUnit node) {
  }

  @Override
  public void endVisit(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    super.endVisit(node);
  }
}