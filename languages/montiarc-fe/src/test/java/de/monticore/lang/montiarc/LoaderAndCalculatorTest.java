package de.monticore.lang.montiarc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Set;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcModelNameCalculator;
import de.monticore.symboltable.Scope;
import org.junit.Test;

/**
 * Created by Michael von Wenckstern on 04.06.2016.
 */
public class LoaderAndCalculatorTest extends AbstractSymtabTest {

  public static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }

  @Test
  public void testModelNameCalculator() {
    MontiArcModelNameCalculator calculator = new MontiArcModelNameCalculator();
    Set<String> models = calculator.calculateModelNames("industry2.turbineController.piController", ConnectorSymbol.KIND);
    System.out.println(models);
    assertEquals(models.size(), 2);
    assertTrue(models.contains("Industry2"));
    assertTrue(models.contains("industry2.TurbineController"));
  }

  // TODO MvW check if bug is in ModelLoader or in SymbolTable resolve
  @Test
  public void testModelLoader() {
    Scope scope = createSymTab("src/test/resources/tags");
    ComponentSymbol sym = scope.<ComponentSymbol>resolve("industry.PIController", ComponentSymbol.KIND).get();
    ComponentSymbol sym2 = scope.<ComponentSymbol>resolve("industry2.PIController", ComponentSymbol.KIND).get();
    assertTrue(sym != sym2);
  }

  // resolves also to the same ComponentSymbol even both are in different packages
  @Test
  public void testSymbolTableResolve() {
    Scope scope = createSymTab("src/test/resources/tags");

    Optional<ComponentSymbol> sym3 = getGlobalScope(scope).<ComponentSymbol>resolveDown("industry.PIController", ComponentSymbol.KIND);
    assertFalse(sym3.isPresent()); // to show that resolveDown does not load any model from ModelLoader
    ComponentSymbol sym = scope.<ComponentSymbol>resolve("industry.PIController", ComponentSymbol.KIND).get();

    Optional<ComponentSymbol> sym2 = getGlobalScope(scope).<ComponentSymbol>resolveDown("industry2.PIController", ComponentSymbol.KIND);
    if (sym2.isPresent())
      System.out.println(sym2.get());
    assertFalse(sym2.isPresent());
  }
}
