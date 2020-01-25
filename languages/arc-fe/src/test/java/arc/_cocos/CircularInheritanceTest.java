/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTComponentBody;
import arc._ast.ASTComponentHead;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymTabMill;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
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
    ASTComponent parent = ArcMill.componentBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcMill.componentHeadBuilder().setParentComponent(
        ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ASTComponent child = ArcMill.componentBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcMill.componentHeadBuilder().setParentComponent(
        ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(parent);
    symTab.handle(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(child);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.CIRCULAR_INHERITANCE });
  }

  @Test
  public void shouldNotFindCircularInheritance() {
    ASTComponent parent = ArcMill.componentBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ASTComponent child = ArcMill.componentBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcMill.componentHeadBuilder().setParentComponent(
        ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
          .build())
        .build())
      .build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(parent);
    symTab.handle(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(parent);
    coco.check(child);
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}