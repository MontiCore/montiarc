/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.TypeExprOfComponent;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InstanceArgsOmitPortReferencesTest extends AbstractTest {

  /**
   * Creates an empty ASTComponentType that is linked to a ComponentTypeSymbol with the same name. Beware that the
   * ComponentTypeSymbol is not part of any scope.
   */
  protected static ASTComponentType provideComponentTypeWithSymbol(@NotNull String name4Comp) {
    Preconditions.checkNotNull(name4Comp);

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(name4Comp)
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(name4Comp)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    comp.setSymbol(sym);
    comp.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(comp);

    return comp;
  }

  @Test
  void shouldFindIllegalPortRef() {
    // Given
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    PortSymbol inPortSym = ArcBasisMill.portSymbolBuilder()
      .setName("inPort")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    PortSymbol outPortSym = ArcBasisMill.portSymbolBuilder()
      .setName("outPort")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(false)
      .build();
    SymbolService.link(comp.getSpannedScope(), inPortSym, outPortSym);

    ASTComponentType subCompType = provideComponentTypeWithSymbol("SubComp");
    ComponentInstanceSymbol inst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("inst")
      .setType(new TypeExprOfComponent(subCompType.getSymbol()))
      .setArcArguments(Lists.newArrayList(
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.nameExpressionBuilder().setName("noPort").build()
        ).build(),
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.nameExpressionBuilder().setName("inPort").build()
        ).build(),
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.nameExpressionBuilder().setName("outPort").build()
        ).build()
      )).build();
    SymbolService.link(comp.getSpannedScope(), inst);

    // When
    InstanceArgsOmitPortReferences coco = new InstanceArgsOmitPortReferences();
    coco.check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.PORT_REFERENCE_IN_INSTANTIATION_ARG_ILLEGAL,
      ArcError.PORT_REFERENCE_IN_INSTANTIATION_ARG_ILLEGAL
    );
  }

  @Test
  void shouldNotFindPortRef() {
    // Given
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    PortSymbol inPortSym = ArcBasisMill.portSymbolBuilder()
      .setName("inPort")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    PortSymbol outPortSym = ArcBasisMill.portSymbolBuilder()
      .setName("outPort")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(false)
      .build();
    SymbolService.link(comp.getSpannedScope(), inPortSym, outPortSym);

    ASTComponentType subCompType = provideComponentTypeWithSymbol("SubComp");
    ComponentInstanceSymbol inst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("inst")
      .setType(new TypeExprOfComponent(subCompType.getSymbol()))
      .setArcArguments(Lists.newArrayList(
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.nameExpressionBuilder().setName("noPort").build()
        ).build(),
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.nameExpressionBuilder().setName("nononoPort").build()
        ).build(),
        ArcBasisMill.arcArgumentBuilder().setExpression(
          ArcBasisMill.literalExpressionBuilder().setLiteral(Mockito.mock(ASTStringLiteral.class)).build()
        ).build()
      )).build();
    SymbolService.link(comp.getSpannedScope(), inst);

    // When
    InstanceArgsOmitPortReferences coco = new InstanceArgsOmitPortReferences();
    coco.check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(/* none */);
  }
}
