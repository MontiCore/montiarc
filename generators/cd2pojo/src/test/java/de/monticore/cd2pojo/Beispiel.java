package de.monticore.cd2pojo;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Beispiel {
  @Test
  public void testResolvingCDTypeSymbol() {
    //Standard Model Path
    ModelPath mp = new ModelPath(Paths.get("src/test/resources/models"));
  
    //Hier haben wir vieles versucht, bspw. domain.Domain.domain.A oder domain.A
    //Der FQN scheint mit domain.Domain beginnen zu müssen, damit das zu parsende Modell überhaupt gefunden werden kann.
    String typeSymbolFQN = "domain.Domain.A";
    
    //Der CDSymTabHelper heißt zwar helper, tatsächlich wird der aber benötigt beim berechnen von Modellnamen. Scheint also nicht optional zu sein.
    CDSymbolTableHelper helper = new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code());
    
    //Standard GlobalScope (mit CDSymTabHelper)
    ICD4CodeGlobalScope a = CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(mp).setModelFileExtension("cd").setSymbolTableHelper(helper).build();
    
    //Standard resolving
    List<CDTypeSymbol> l = a.resolveCDTypeMany(typeSymbolFQN);
    
    //Der Test sollte auch fehlschlagen können
    assertEquals(1,l.size(),
      "Es sollte exakt ein Symbol mit qualifiziertem Namen " + typeSymbolFQN + " gefunden werden. " +
        "Es wurden " + l.size() + " Symbole gefunden.");
  }
  
  @Test
  public void testCDResolvingWithTypeSymbolResolvingHelper() {
    ICD4CodeGlobalScope cd4cGS = POJOGeneratorTool
      .getLoadedICD4CodeGlobalScope(Collections.singleton(Paths.get("src/test/resources/models")));
    List<CDRoleSymbol> aRoleSymbols =
      cd4cGS.resolveCDTypeMany("simple.A").get(0).getCDRoleList();
    List<CDRoleSymbol> bRoleSymbols =
      cd4cGS.resolveCDTypeMany("simple.B").get(0).getCDRoleList();
    System.out.println(aRoleSymbols.size() + bRoleSymbols.size());
  }
  
  @Test
  public void how2ResolveCD() {
    ModelPath mp = new ModelPath(Paths.get("src/test/resources/models"));
    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(mp)
      .setModelFileExtension("cd").build();
    List<CDRoleSymbol> aRoleSymbols =
      cd4CGlobalScope.resolveCDTypeMany("simple.simple.A").get(0).getCDRoleList();
    System.out.println(aRoleSymbols.size());
  }
}