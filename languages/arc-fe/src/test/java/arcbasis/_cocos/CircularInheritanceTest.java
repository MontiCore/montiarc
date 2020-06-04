/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymTabMill;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link CircularInheritance}.
 */
public class CircularInheritanceTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldFindCircularInheritance() {
    ASTComponentType parent = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParentComponent(
        ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ASTComponentType child = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParentComponent(
        ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
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
    ASTComponentType parent = ArcBasisMill.componentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ASTComponentType child = ArcBasisMill.componentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setParentComponent(
        ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(parent);
    symTab.handle(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(parent);
    coco.check(child);
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}