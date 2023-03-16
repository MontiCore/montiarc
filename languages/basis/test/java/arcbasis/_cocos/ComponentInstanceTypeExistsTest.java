/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceTypeExists}.
 */
public class ComponentInstanceTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("A");
    ASTComponentInstantiation instantiation = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ASTComponentType enclType = arcbasis.ArcBasisMill.componentTypeBuilder()
      .setName("EnclType")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().addArcElement(instantiation).build())
      .build();
    ArcBasisScopesGenitorDelegator genitor = ArcBasisMill.scopesGenitorDelegator();
    genitor.createFromAST(enclType).setName("Scopy");

    // When
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(instantiation);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE);
  }

  @Test
  public void shouldFindMissingArcFieldAssignment() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("A");
    ASTComponentInstantiation instantiation = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ASTComponentType enclType = arcbasis.ArcBasisMill.componentTypeBuilder()
      .setName("EnclType")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().addArcElement(instantiation).build())
      .build();
    ArcBasisScopesGenitorDelegator genitor = ArcBasisMill.scopesGenitorDelegator();
    genitor.createFromAST(enclType).setName("Scopy");
    enclType.getSpannedScope().add(ArcBasisMill.typeSymbolBuilder().setName("A").build());

    // When
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(instantiation);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_ASSIGNMENT_OF_ARC_FIELD);
  }

  @Test
  public void shouldFindTooManyTypes() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("A");
    ASTComponentInstantiation instantiation = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ASTComponentType enclType = arcbasis.ArcBasisMill.componentTypeBuilder()
      .setName("EnclType")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().addArcElement(instantiation).build())
      .build();
    ArcBasisScopesGenitorDelegator genitor = ArcBasisMill.scopesGenitorDelegator();
    genitor.createFromAST(enclType).setName("Scopy");

    enclType.getSpannedScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());
    enclType.getSpannedScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());

    // When
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(instantiation);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.SYMBOL_TOO_MANY_FOUND);
  }

  @Test
  public void shouldFindType() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("A");
    ASTComponentInstantiation instantiation = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ASTComponentType enclType = arcbasis.ArcBasisMill.componentTypeBuilder()
      .setName("EnclType")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().addArcElement(instantiation).build())
      .build();
    ArcBasisScopesGenitorDelegator genitor = ArcBasisMill.scopesGenitorDelegator();
    genitor.createFromAST(enclType).setName("Scopy");

    enclType.getSpannedScope().add(ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build());

    // When
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(instantiation);

    // Then
    this.checkOnlyExpectedErrorsPresent();
  }
}