/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import montiarc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link InheritedComponentTypeExists}.
 */
public class InheritedComponentTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    // Given
    ASTMCQualifiedType parent = createQualifiedType("A");
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(ast);

    // When
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT);
  }

  @Test
  public void shouldFindTooManyTypes() {
    // Given
    ASTMCQualifiedType parent = createQualifiedType("A");
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(ast);
    ast.getEnclosingScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());
    ast.getEnclosingScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());

    // When
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.SYMBOL_TOO_MANY_FOUND);
  }

  @Test
  public void shouldFindType() {
    // Given
    ASTMCQualifiedType parent = createQualifiedType("A");
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(ast);
    ast.getEnclosingScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());

    // When
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent();
  }
}