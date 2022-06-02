/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

import comfortablearc._symboltable.ComfortableArcSymbolTableCompleter;
import comfortablearc._symboltable.ComfortableArcSymbolTableCompleterDelegator;

public class ComfortableArcMill extends ComfortableArcMillTOP {

  protected static ComfortableArcMill millComfortableArcSymbolTableCompleter ;

  protected static ComfortableArcMill millComfortableArcSymbolTableCompleterDelegator;

  public static ComfortableArcSymbolTableCompleter symbolTableCompleter ()  {
    if (millComfortableArcSymbolTableCompleter == null) {
      millComfortableArcSymbolTableCompleter = getMill();
    }
    return millComfortableArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected ComfortableArcSymbolTableCompleter _symbolTableCompleter() {
    return new ComfortableArcSymbolTableCompleter();
  }

  public static ComfortableArcSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millComfortableArcSymbolTableCompleterDelegator == null) {
      millComfortableArcSymbolTableCompleterDelegator = getMill();
    }
    return millComfortableArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected ComfortableArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new ComfortableArcSymbolTableCompleterDelegator();
  }

  public static void initMe(ComfortableArcMill a)  {
    ComfortableArcMillTOP.initMe(a);
    millComfortableArcSymbolTableCompleter = a;
    millComfortableArcSymbolTableCompleterDelegator = a;
  }

  public static void reset() {
    ComfortableArcMillTOP.reset();
    millComfortableArcSymbolTableCompleter = null;
    millComfortableArcSymbolTableCompleterDelegator = null;
  }
}