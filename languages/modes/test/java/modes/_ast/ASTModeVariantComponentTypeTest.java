/* (c) https://github.com/MontiCore/monticore */
package modes._ast;

import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import modes.ModesMill;
import modes.ModesTestBase;
import modes._symboltable.ArcModeSymbol;
import modes._symboltable.ModesVariantComponentTypeSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

public class ASTModeVariantComponentTypeTest extends ModesTestBase {

  @Test
  public void shouldNotBuildASTVariant() {
    // Given
    ArcModeSymbol mode = ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(ModesMill.scope()).build();
    ASTArcComponentType astComponentType = ModesMill.arcComponentTypeBuilder().setName("C").setHead(Mockito.mock(ASTComponentHead.class)).setBody(ModesMill.componentBodyBuilder().build()).build();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setAstNode(astComponentType).setEnclosingScope(ModesMill.scope()).setSpannedScope(ModesMill.scope()).build(), mode);

    // Then
    Assertions.assertFalse(variant.isPresentAstNode());
  }

  @Test
  public void shouldNotBuildASTVariant2() {
    // Given
    ASTArcMode astMode = ModesMill.arcModeBuilder().setName("m1").setBody(ModesMill.componentBodyBuilder().build()).build();
    ArcModeSymbol mode = ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(ModesMill.scope()).setAstNode(astMode).build();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setEnclosingScope(ModesMill.scope()).setSpannedScope(ModesMill.scope()).build(), mode);

    // Then
    Assertions.assertFalse(variant.isPresentAstNode());
  }

  @Test
  public void shouldBuildASTVariant() {
    // Given
    ASTArcMode astMode = ModesMill.arcModeBuilder().setName("m1").setBody(ModesMill.componentBodyBuilder().build()).build();
    ArcModeSymbol mode = ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(ModesMill.scope()).setAstNode(astMode).build();
    ASTArcComponentType astComponentType = ModesMill.arcComponentTypeBuilder().setName("C").setHead(Mockito.mock(ASTComponentHead.class)).setBody(ModesMill.componentBodyBuilder().build()).build();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setAstNode(astComponentType).setEnclosingScope(ModesMill.scope()).setSpannedScope(ModesMill.scope()).build(), mode);

    // Then
    Assertions.assertTrue(variant.isPresentAstNode());
    Assertions.assertNotNull(variant.getAstNode());
    Assertions.assertInstanceOf(ASTModeVariantComponentType.class, variant.getAstNode());
  }

  @Test
  public void shouldAddASTConnectors() {
    // Given
    ASTConnector connector = ModesMill.connectorBuilder().setSource("a").build();
    ASTArcMode astMode = ModesMill.arcModeBuilder().setName("m1").setBody(ModesMill.componentBodyBuilder().setArcElementsList(Collections.singletonList(connector)).build()).build();
    ArcModeSymbol mode = ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(ModesMill.scope()).setAstNode(astMode).build();
    ASTArcComponentType astComponentType = ModesMill.arcComponentTypeBuilder().setName("C").setHead(Mockito.mock(ASTComponentHead.class)).setBody(ModesMill.componentBodyBuilder().build()).build();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setAstNode(astComponentType).setEnclosingScope(ModesMill.scope()).setSpannedScope(ModesMill.scope()).build(), mode);

    // When
    List<ASTConnector> returnedConnectors = ((ASTArcComponentType) variant.getAstNode()).getConnectors();

    // Then
    Assertions.assertEquals(1, returnedConnectors.size());
  }

}
