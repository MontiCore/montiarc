/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcTraverser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Holds test for {@link MontiArcScopesGenitor}.
 */
public class MontiArcScopesGenitorTest extends AbstractTest {

  protected final MontiArcParser parser = new MontiArcParser();
  protected final MontiArcScopesGenitor genitor = new MontiArcScopesGenitor();

  protected MontiArcParser getParser() {
    return this.parser;
  }

  protected MontiArcScopesGenitor getGenitor() {
    return this.genitor;
  }

  @Test
  public void createFromASTShouldSetEnclosingScope() throws IOException {
    //Given
    Optional<ASTMACompilationUnit> ast = this.getParser().parse_String("component A { }");
    Preconditions.checkState(ast.isPresent());
    IMontiArcScope scope = MontiArcMill.scope();
    this.getGenitor().putOnStack(scope);
    this.getGenitor().setTraverser(new MontiArcTraverser() {});

    //When
    this.getGenitor().createFromAST(ast.get());

    //Then
    Assertions.assertNotNull(ast.get().getEnclosingScope());
    Assertions.assertEquals(scope, ast.get().getEnclosingScope());
  }

  @Test
  public void createFromASTShouldNotSetEnclosingScope() throws IOException {
    Preconditions.checkState(!this.getGenitor().getCurrentScope().isPresent());
    //Given
    Optional<ASTMACompilationUnit> ast = this.getParser().parse_String("component A { }");
    Preconditions.checkState(ast.isPresent());
    this.getGenitor().setTraverser(new MontiArcTraverser() {});

    //When
    this.getGenitor().createFromAST(ast.get());

    //Then
    Assertions.assertNull(ast.get().getEnclosingScope());
  }
}