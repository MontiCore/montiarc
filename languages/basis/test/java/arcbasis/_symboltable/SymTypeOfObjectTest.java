/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;

/**
 * Holds tests for {@link SymTypeOfObject}.
 */
public class SymTypeOfObjectTest extends ArcBasisAbstractTest {

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.setUpTypes();
  }

  public void setUpTypes() {
    // create scope a.b.c
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("a.b.c");

    // create type symbol a.b.c.X
    TypeSymbol symbol = ArcBasisMill.typeSymbolBuilder().setName("X")
      .setEnclosingScope(as).setSpannedScope(ArcBasisMill.scope()).build();
    as.add(symbol);
  }

  /**
   * The method under test is {@link SymTypeOfObject#print()}.
   */
  @Test
  public void shouldPrintName1() {
    // Given
    SymTypeExpression type = createTypeObject(ArcBasisMill.globalScope().resolveType("a.b.c.X").orElseThrow());

    // When && Then
    Assertions.assertThat(type.print()).isEqualTo("X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#print()}.
   */
  @Test
  public void shouldPrintName2() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    as.setImportsList(Collections.singletonList(new ImportStatement("a.b.c.X", false)));

    SymTypeExpression type = createTypeObject("X", as);

    // When && Then
    Assertions.assertThat(type.print()).isEqualTo("X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#print()}.
   */
  @Test
  public void shouldPrintName3() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Z");

    SymTypeExpression type = createTypeObject("a.b.c.X", as);

    // When && Then
    Assertions.assertThat(type.print()).isEqualTo("a.b.c.X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#print()}.
   */
  @Test
  public void shouldPrintName4() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    SymTypeExpression type = createTypeObject("W", as);

    // When && Then
    Assertions.assertThat(type.print()).isEqualTo("W");
  }

  /**
   * The method under test is {@link SymTypeOfObject#print()}.
   */
  @Test
  public void shouldPrintName5() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    SymTypeExpression type = createTypeObject("a.b.c.W", as);

    // When && Then
    Assertions.assertThat(type.print()).isEqualTo("a.b.c.W");
  }

  /**
   * The method under test is {@link SymTypeOfObject#printFullName()}.
   */
  @Test
  public void shouldPrintFullName1() {
    // Given
    SymTypeExpression type = createTypeObject(ArcBasisMill.globalScope().resolveType("a.b.c.X").orElseThrow());

    // When && Then
    Assertions.assertThat(type.printFullName()).isEqualTo("a.b.c.X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#printFullName()}.
   */
  @Test
  public void shouldPrintFullName2() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    as.setImportsList(Collections.singletonList(new ImportStatement("a.b.c.X", false)));

    SymTypeExpression type = createTypeObject("X", as);

    // When && Then
    Assertions.assertThat(type.printFullName()).isEqualTo("a.b.c.X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#printFullName()}.
   */
  @Test
  public void shouldPrintFullName3() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    as.setImportsList(Collections.singletonList(new ImportStatement("a.b.c.X", false)));

    SymTypeExpression type = createTypeObject("a.b.c.X", as);

    // When && Then
    Assertions.assertThat(type.printFullName()).isEqualTo("a.b.c.X");
  }

  /**
   * The method under test is {@link SymTypeOfObject#printFullName()}.
   */
  @Test
  public void shouldPrintFullName4() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    SymTypeExpression type = createTypeObject("W", as);

    // When && Then
    Assertions.assertThat(type.printFullName()).isEqualTo("W");
  }

  /**
   * The method under test is {@link SymTypeOfObject#printFullName()}.
   */
  @Test
  public void shouldPrintFullName5() {
    // Given
    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    ArcBasisMill.globalScope().addSubScope(as);
    as.setEnclosingScope(ArcBasisMill.globalScope());
    as.setPackageName("d.e.f");
    as.setName("Y");
    SymTypeExpression type = createTypeObject("a.b.c.W", as);

    // When && Then
    Assertions.assertThat(type.printFullName()).isEqualTo("a.b.c.W");
  }
}