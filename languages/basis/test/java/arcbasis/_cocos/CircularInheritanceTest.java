/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
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

import java.util.Collections;

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
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(
        arcbasis.ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          arcbasis.ArcBasisMill.mCQualifiedNameBuilder().setPartsList(Collections.singletonList(
            "A")).build())
          .build())
        .build())
      .build();
    ArcBasisScopesGenitorDelegator genitor = ArcBasisMill.scopesGenitorDelegator();
    genitor.createFromAST(parent);
    genitor.createFromAST(child);
    ArcBasisSymbolTableCompleterDelegator completer = ArcBasisMill.symbolTableCompleterDelegator();
    completer.createFromAST(parent);
    completer.createFromAST(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(child);
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CIRCULAR_INHERITANCE});
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
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(parent);
    symTab.createFromAST(child);
    CircularInheritance coco = new CircularInheritance();
    coco.check(parent);
    coco.check(child);
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}