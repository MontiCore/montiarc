/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link CircularInheritance}.
 */
public class CircularInheritanceTest extends AbstractTest {

  @Test
  public void shouldFindCircularInheritance() {
    ASTComponentType parent = arcbasis.ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();
    ASTComponentType child = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();
    ArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(parent);
    symTab.handle(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(child);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.CIRCULAR_INHERITANCE });
  }

  @Test
  public void shouldNotFindCircularInheritance() {
    ASTComponentType parent = arcbasis.ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ASTComponentType child = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(createQualifiedType("A")).build())
      .build();
    ArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(parent);
    symTab.handle(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(parent);
    coco.check(child);
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}