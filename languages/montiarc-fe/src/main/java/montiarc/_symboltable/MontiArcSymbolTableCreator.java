/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MontiArcSymbolTableCreator extends MontiArcSymbolTableCreatorTOP {

  public MontiArcSymbolTableCreator() {
    super(new MontiArcGlobalScope());
  }

  public MontiArcSymbolTableCreator(@NotNull IMontiArcScope enclosingScope) {
    super(enclosingScope);
  }

<<<<<<< HEAD
  MontiArcScope watchscope;
=======
  IMontiArcScope watchscope;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772

  public MontiArcSymbolTableCreator(
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
<<<<<<< HEAD
    MontiArcArtifactScope artifactScope = montiarc.MontiArcMill.montiArcArtifactScopeBuilder()
      .setPackageName(rootNode.getPackage().getQName())
=======
    IMontiArcArtifactScope artifactScope = montiarc.MontiArcMill.montiArcArtifactScopeBuilder()
      .setPackageName(rootNode.isPresentPackage() ? rootNode.getPackage().getQName() : "")
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
      .setImportsList(imports)
      .build();
    putOnStack(artifactScope);
    watchscope = artifactScope;
    setLinkBetweenSpannedScopeAndNode(artifactScope, rootNode);
    rootNode.accept(getRealThis());
    removeCurrentScope();
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