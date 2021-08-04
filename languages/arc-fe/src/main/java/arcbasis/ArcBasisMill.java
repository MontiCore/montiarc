/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;

public class ArcBasisMill extends ArcBasisMillTOP {

  protected static ArcBasisMill millArcBasisSymbolTableCompleter ;

  protected static ArcBasisMill millArcBasisSymbolTableCompleterDelegator;

  public static ArcBasisSymbolTableCompleter symbolTableCompleter ()  {
    if (millArcBasisSymbolTableCompleter == null) {
      millArcBasisSymbolTableCompleter = getMill();
    }
    return millArcBasisSymbolTableCompleter._symbolTableCompleter();
  }

  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter();
  }

  public static ArcBasisSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millArcBasisSymbolTableCompleterDelegator == null) {
      millArcBasisSymbolTableCompleterDelegator = getMill();
    }
    return millArcBasisSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected ArcBasisSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new ArcBasisSymbolTableCompleterDelegator();
  }

  public static void initMe(ArcBasisMill a)  {
    ArcBasisMillTOP.initMe(a);
    millArcBasisSymbolTableCompleter = a;
    millArcBasisSymbolTableCompleterDelegator = a;
  }

  public static void reset() {
    ArcBasisMillTOP.reset();
    millArcBasisSymbolTableCompleter = null;
    millArcBasisSymbolTableCompleterDelegator = null;
  }
}