/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * Holds tests for {@link ArcBasisSymbolTableCompleter}.
 */
public class ArcBasisSymbolTableCompleterTest extends AbstractTest {

  /**
   * Encapsulates the {@link ArcBasisSymbolTableCompleter} under tests, attaching it to a traverser.
   */
  protected ArcBasisSymbolTableCompleterDelegator4Tests completerDelegator;

  @BeforeEach
  public void setUpCompleter() {
    this.completerDelegator = new ArcBasisSymbolTableCompleterDelegator4Tests();
  }

  /**
   * @see this#completerDelegator
   */
  protected ArcBasisSymbolTableCompleterDelegator4Tests getCompleterDelegator() {
    return this.completerDelegator;
  }

  /**
   * Returns the unit under test: a {@link ArcBasisSymbolTableCompleter}
   */
  protected ArcBasisSymbolTableCompleter getCompleter() {
    return this.getCompleterDelegator().getCompleter();
  }

  @Test
  public void shouldVisitComponentType() {
    // Given
    ASTComponentType compAst = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();

    ComponentTypeSymbol compSym = ArcBasisMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(Mockito.mock(IArcBasisScope.class))
        .build();

    compAst.setSymbol(compSym);

    // When
    getCompleter().visit(compAst);

    // Then
    Assertions.assertEquals(1, getCompleter().getComponentStack().size());
    Assertions.assertEquals(compSym, getCompleter().getCurrentComponent().get());
  }

  @Test
  public void shouldEndVisitComponentType() {
    // Given
    ASTComponentType compAst = ArcBasisMill.componentTypeBuilder()
        .setName("Comp")
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build();

    ComponentTypeSymbol compSym = ArcBasisMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(Mockito.mock(IArcBasisScope.class))
        .build();

    compSym.setAstNode(compAst);
    getCompleter().putOnStack(compSym);

    // When
    getCompleter().endVisit(compAst);

    // Then
    Assertions.assertEquals(0, getCompleter().getComponentStack().size());
  }

  @Test
  public void shouldVisitComponentHead() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol parentComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol symChildComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    ASTComponentHead childCompHead = ArcBasisMill.componentHeadBuilder()
      .setParent(createQualifiedType(parentCompName))
      .build();

    ASTComponentType astChildComp = ArcBasisMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(childCompHead)
      .build();

    ArcBasisMill.globalScope().add(parentComp);
    ArcBasisMill.globalScope().add(symChildComp);

    symChildComp.setAstNode(astChildComp);
    astChildComp.setSymbol(symChildComp);
    astChildComp.setEnclosingScope(ArcBasisMill.globalScope());
    childCompHead.setEnclosingScope(ArcBasisMill.globalScope());

    getCompleter().putOnStack(symChildComp);

    // When
    getCompleter().visit(childCompHead);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent());
    Assertions.assertEquals(1, getCompleter().getComponentStack().size());
    Assertions.assertEquals(symChildComp, getCompleter().getCurrentComponent().get());
  }

  @Test
  public void shouldCompleteComponentTypeParent() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol parentComp = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(parentCompName)
        .setSpannedScope(Mockito.mock(IArcBasisScope.class))
        .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol symChildComp = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(childCompName)
        .setSpannedScope(Mockito.mock(IArcBasisScope.class))
        .build();

    ASTComponentHead childCompHead = ArcBasisMill.componentHeadBuilder()
        .setParent(createQualifiedType(parentCompName))
        .build();

    ASTComponentType astChildComp = ArcBasisMill.componentTypeBuilder()
        .setName(childCompName)
        .setBody(Mockito.mock(ASTComponentBody.class))
        .setHead(childCompHead)
        .build();

    ArcBasisMill.globalScope().add(parentComp);
    ArcBasisMill.globalScope().add(symChildComp);

    symChildComp.setAstNode(astChildComp);
    astChildComp.setSymbol(symChildComp);
    astChildComp.setEnclosingScope(ArcBasisMill.globalScope());
    childCompHead.setEnclosingScope(ArcBasisMill.globalScope());

    // When
    getCompleterDelegator().createFromAST(astChildComp);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent());
    Assertions.assertEquals(0, getCompleter().getComponentStack().size());
  }

  /**
   * Grants access to encapsulated symbolTableCompleter used by the symbolTableCompleterDelegator.
   */
  private static class ArcBasisSymbolTableCompleterDelegator4Tests extends ArcBasisSymbolTableCompleterDelegator {
    public ArcBasisSymbolTableCompleter getCompleter() {
      Preconditions.checkState(this.getTraverser().getArcBasisHandler().isPresent());
      Preconditions.checkState(this.getTraverser().getArcBasisHandler().get() instanceof ArcBasisSymbolTableCompleter);
      return (ArcBasisSymbolTableCompleter) this.getTraverser().getArcBasisHandler().get();
    }
  }
}