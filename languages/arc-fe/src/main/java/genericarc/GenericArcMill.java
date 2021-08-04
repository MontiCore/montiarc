/* (c) https://github.com/MontiCore/monticore */
package genericarc;

import genericarc._symboltable.GenericArcSymbolTableCompleter;
import genericarc._symboltable.GenericArcSymbolTableCompleterDelegator;

public class GenericArcMill extends GenericArcMillTOP {

  protected static GenericArcMill millGenericArcSymbolTableCompleter ;

  protected static GenericArcMill millGenericArcSymbolTableCompleterDelegator;

  public static GenericArcSymbolTableCompleter symbolTableCompleter ()  {
    if (millGenericArcSymbolTableCompleter == null) {
      millGenericArcSymbolTableCompleter = getMill();
    }
    return millGenericArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected GenericArcSymbolTableCompleter _symbolTableCompleter() {
    return new GenericArcSymbolTableCompleter();
  }

  public static GenericArcSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millGenericArcSymbolTableCompleterDelegator == null) {
      millGenericArcSymbolTableCompleterDelegator = getMill();
    }
    return millGenericArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected GenericArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new GenericArcSymbolTableCompleterDelegator();
  }

  public static void initMe(GenericArcMill a)  {
    GenericArcMillTOP.initMe(a);
    millGenericArcSymbolTableCompleter = a;
    millGenericArcSymbolTableCompleterDelegator = a;
  }

  public static void reset() {
    GenericArcMillTOP.reset();
    millGenericArcSymbolTableCompleter = null;
    millGenericArcSymbolTableCompleterDelegator = null;
  }
}