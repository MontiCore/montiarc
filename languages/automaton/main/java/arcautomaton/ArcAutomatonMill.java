/* (c) https://github.com/MontiCore/monticore */
package arcautomaton;

import arcautomaton._symboltable.ArcAutomatonScopesGenitorP2;
import de.monticore.scevents.SCEventsMill;
import de.monticore.scevents._symboltable.SCEventDefSymbol;

public class ArcAutomatonMill extends ArcAutomatonMillTOP {
  
  protected static ArcAutomatonMill tickInitializer;
  
  protected static ArcAutomatonMill millArcAutomatonScopesGenitorP2;
  
  public static void initMe(ArcAutomatonMill mill) {
    ArcAutomatonMillTOP.initMe(mill);
    tickInitializer = mill;
    millArcAutomatonScopesGenitorP2 = mill;
  }
  
  public final static String TICK = "Tick";
  
  public static void initializeTick() {
    if(tickInitializer == null) {
      tickInitializer = getMill();
    }
    
    tickInitializer._initializeTick();
  }
  
  public void _initializeTick() {
    SCEventDefSymbol tick = SCEventsMill.sCEventDefSymbolBuilder()
        .setName(TICK)
        .setFullName(TICK)
        .setEnclosingScope(globalScope())
        .build();
    globalScope().add(tick);
  }
  
  public static ArcAutomatonScopesGenitorP2 scopesGenitorP2() {
    if (millArcAutomatonScopesGenitorP2 == null) {
      millArcAutomatonScopesGenitorP2 = getMill();
    }
    return millArcAutomatonScopesGenitorP2._scopesGenitorP2();
  }
  
  protected ArcAutomatonScopesGenitorP2 _scopesGenitorP2() {
    return new ArcAutomatonScopesGenitorP2();
  }
  
  public static void reset() {
    ArcAutomatonMillTOP.reset();
    tickInitializer = null;
    millArcAutomatonScopesGenitorP2 = null;
  }
  
}
