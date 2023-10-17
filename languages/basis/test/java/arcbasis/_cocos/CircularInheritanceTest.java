/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP2Delegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP3Delegator;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

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
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(ast);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(ast);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(ast);

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
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("B")).build())).build())
      .build();
    ASTComponentType b = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(a);
    stg.createFromAST(b);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(a);
    stc.createFromAST(b);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(a);
    st3.createFromAST(b);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(a);

    // Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE});
  }

  /**
   * If a component transitively  and directly extends itself, then the context-condition
   * should report two errors.
   */
  @Test
  public void shouldFindMultipleInheritedCircularInheritance() {
    // Given
    ASTComponentType a = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(List.of(
        ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build(),
        ArcBasisMill.arcParentBuilder().setType(createQualifiedType("B")).build())).build())
      .build();
    ASTComponentType b = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(a);
    stg.createFromAST(b);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(a);
    stc.createFromAST(b);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(a);
    st3.createFromAST(b);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(a);

    // Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE, ArcError.CIRCULAR_INHERITANCE});
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
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();
    ASTComponentType child = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(parent);
    st3.createFromAST(child);

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
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParentsList(Collections.singletonList(ArcBasisMill.arcParentBuilder().setType(createQualifiedType("A")).build())).build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(parent);
    st3.createFromAST(child);

    CircularInheritance coco = new CircularInheritance();

    // When
    coco.check(parent);
    coco.check(child);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }
}