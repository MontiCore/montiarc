package de.monticore.lang.montiarc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcModelNameCalculator;
import de.monticore.symboltable.Scope;

/**
 * Created by Michael von Wenckstern on 04.06.2016.
 */
public class LoaderAndCalculatorTest extends AbstractSymtabTest {
  
  private static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }
  
  @Ignore("TODO MvW<-RH documentation of ModelLoader and clarify issue #2")
  @Test
  public void testModelNameCalculator() {
    MontiArcModelNameCalculator calculator = new MontiArcModelNameCalculator();
    Set<String> models = calculator.calculateModelNames("industry2.turbineController.piController",
        ConnectorSymbol.KIND);
    System.out.println(models);
    assertEquals(models.size(), 2);
    assertTrue(models.contains("Industry2"));
    assertTrue(models.contains("industry2.TurbineController"));
  }
  
  // TODO MvW check if bug is in ModelLoader or in SymbolTable resolve
  @Test
  public void testModelLoader() {
    Scope scope = createSymTab("src/test/resources/arc4/symtab/modelloader");
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve("industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = scope
        .<ComponentSymbol> resolve("industry2.PIController", ComponentSymbol.KIND);
    assertTrue(sym2.isPresent());
    
    assertTrue(sym.get() != sym2.get());
  }
  
  // resolves also to the same ComponentSymbol even both are in different packages
  @Test
  public void testSymbolTableResolve() {
    Scope scope = createSymTab("src/test/resources/arc4/symtab/modelloader");
    
    Optional<ComponentSymbol> sym3 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("industry.PIController", ComponentSymbol.KIND);
    assertFalse("resolveDown may not load any model from ModelLoader", sym3.isPresent());
    
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve("industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("industry2.PIController", ComponentSymbol.KIND);
    assertFalse(sym2.isPresent());
    
  }
}
