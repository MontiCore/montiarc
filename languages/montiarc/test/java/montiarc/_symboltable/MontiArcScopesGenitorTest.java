/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * The class under test is {@link MontiArcScopesGenitor}.
 */
public class MontiArcScopesGenitorTest extends MontiArcAbstractTest {

  /**
   * The method under test is {@link MontiArcScopesGenitor#visit(ASTMACompilationUnit)}
   */
  @Test
  public void shouldSetEnclosingScope() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(scope);

    // When
    genitor.visit(ast);

    // Then
    Assertions.assertThat(ast.getEnclosingScope()).isNotNull();
    Assertions.assertThat(ast.getEnclosingScope()).isEqualTo(scope);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#visit(ASTMACompilationUnit)}
   */
  @Test
  public void shouldWarnMissingEnclosingScope() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);

    // When
    genitor.visit(ast);

    // Then
    Assertions.assertThat(ast.getEnclosingScope()).isNull();
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(1);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#visit(ASTMACompilationUnit)}
   */
  @Test
  public void shouldRetainScopeStack() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(scope);

    // When
    genitor.visit(ast);

    // Then
    Assertions.assertThat(genitor.getCurrentScope()).isPresent();
    Assertions.assertThat(genitor.getCurrentScope().get()).isEqualTo(scope);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#createFromAST(ASTMACompilationUnit)}
   */
  @Test
  public void shouldCreateArtifactScope() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(gs);

    int size = gs.getSubScopes().size();

    // When
    IMontiArcArtifactScope as = genitor.createFromAST(ast);

    // Then
    Assertions.assertThat(gs.getSubScopes().size()).isEqualTo(size + 1);
    Assertions.assertThat(gs.getSubScopes().get(size)).isEqualTo(as);
    Assertions.assertThat(as.getEnclosingScope()).isEqualTo(gs);
    Assertions.assertThat(as.getPackageName()).isEqualTo("");
    Assertions.assertThat(as.getImportsList().size()).isEqualTo(0);
    Assertions.assertThat(as.getAstNode()).isEqualTo(ast);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#createFromAST(ASTMACompilationUnit)}
   */
  @Test
  public void shouldSetImportStatement() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .addImportStatement(MontiArcMill.mCImportStatementBuilder()
        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
          .addParts("a").addParts("b").addParts("C").build())
        .setStar(false)
        .build())
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(gs);

    // When
    IMontiArcArtifactScope as = genitor.createFromAST(ast);

    // Then
    Assertions.assertThat(as.getPackageName()).isEqualTo("");
    Assertions.assertThat(as.getImportsList().size()).isEqualTo(1);
    Assertions.assertThat(as.getImports(0).getStatement()).isEqualTo("a.b.C");
    Assertions.assertThat(as.getImports(0).isStar()).isEqualTo(false);
    Assertions.assertThat(as.getAstNode()).isEqualTo(ast);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#createFromAST(ASTMACompilationUnit)}
   */
  @Test
  public void shouldSetImportStatements() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .addImportStatement(MontiArcMill.mCImportStatementBuilder()
        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
          .addParts("a").addParts("b").addParts("C").build())
        .setStar(true)
        .build())
      .addImportStatement(MontiArcMill.mCImportStatementBuilder()
        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
          .addParts("a").addParts("b").addParts("D").build())
        .setStar(false)
        .build())
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(gs);

    // When
    IMontiArcArtifactScope as = genitor.createFromAST(ast);

    // Then
    Assertions.assertThat(as.getPackageName()).isEqualTo("");
    Assertions.assertThat(as.getImportsList().size()).isEqualTo(2);
    Assertions.assertThat(as.getImports(0).getStatement()).isEqualTo("a.b.C");
    Assertions.assertThat(as.getImports(0).isStar()).isEqualTo(true);
    Assertions.assertThat(as.getImports(1).getStatement()).isEqualTo("a.b.D");
    Assertions.assertThat(as.getImports(1).isStar()).isEqualTo(false);
    Assertions.assertThat(as.getAstNode()).isEqualTo(ast);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#createFromAST(ASTMACompilationUnit)}
   */
  @Test
  public void shouldSetPackage() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setPackage(MontiArcMill.mCQualifiedNameBuilder()
        .addParts("a").addParts("b").build())
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(gs);

    // When
    IMontiArcArtifactScope as = genitor.createFromAST(ast);

    // Then
    Assertions.assertThat(as.getPackageName()).isEqualTo("a.b");
    Assertions.assertThat(as.getImportsList().size()).isEqualTo(0);
    Assertions.assertThat(as.getAstNode()).isEqualTo(ast);
  }

  /**
   * The method under test is {@link MontiArcScopesGenitor#createFromAST(ASTMACompilationUnit)}
   */
  @Test
  public void shouldRetainScopeStack2() {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.mACompilationUnitBuilder()
      .setComponentType(MontiArcMill.componentTypeBuilder()
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setName("Comp").build())
      .build();
    MontiArcScopesGenitor genitor = MontiArcMill.scopesGenitor();
    IMontiArcGlobalScope scope = MontiArcMill.globalScope();
    genitor.setTraverser(MontiArcMill.traverser());
    genitor.getTraverser().add4MontiArc(genitor);
    genitor.putOnStack(scope);

    // When
    genitor.createFromAST(ast);

    // Then
    Assertions.assertThat(genitor.getCurrentScope()).isPresent();
    Assertions.assertThat(genitor.getCurrentScope().get()).isEqualTo(scope);
  }
}