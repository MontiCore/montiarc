/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link CircularInheritance}.
 */
public class CircularInheritanceTest extends ArcBasisAbstractTest {

  /**
   * If a component directly extends itself, then the context-condition should
   * report an error.
   */
  @Test
  public void shouldFindDirectCircularInheritance() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(ast);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(ast);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE});
  }

  /**
   * If a component transitively extends itself, then the context-condition
   * should report an error.
   */
  @Test
  public void shouldFindTransitiveCircularInheritance() {
    // Given
    ASTComponentType a = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("B")).build())
      .build();
    ASTComponentType b = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(a);
    stg.createFromAST(b);
    ArcBasisSymbolTableCompleterDelegator stc = ArcBasisMill.symbolTableCompleterDelegator();
    stc.createFromAST(a);
    stc.createFromAST(b);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(a);

    // Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE});
  }

  /**
   * If a component does not circularly extend itself, then the
   * context-condition should not report an error, even if one of the
   * component's parents extends itself.
   */
  @Test
  public void shouldNotReportCircularInheritance() {
    // Given
    ASTComponentType parent = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();
    ASTComponentType child = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisSymbolTableCompleterDelegator stc = ArcBasisMill.symbolTableCompleterDelegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(parent);

    // Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE});
  }

  /**
   * If there is no circular inheritance, then the context-condition should
   * not report an error.
   */
  @Test
  public void shouldNotFindCircularInheritance() {
    // Given
    ASTComponentType parent = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ASTComponentType child = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisSymbolTableCompleterDelegator stc = ArcBasisMill.symbolTableCompleterDelegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(parent);
    coco.check(child);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }
}