/* (c) https://github.com/MontiCore/monticore */
package genericarc;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import genericarc._symboltable.GenericArcSymbolTableCompleterDelegator;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class GenericArcMill extends GenericArcMillTOP {

  protected static GenericArcMill millGenericArcSymbolTableCompleter ;

  protected static GenericArcMill millGenericArcSymbolTableCompleterDelegator;

  protected static GenericArcMill millFullPrettyPrinter;

  protected static GenericArcMill millCompTypeExprDeSer;

  public static GenericArcSymbolTableCompleter symbolTableCompleter ()  {
    if (millGenericArcSymbolTableCompleter == null) {
      millGenericArcSymbolTableCompleter = GenericArcMillTOP.getMill();
    }
    return millGenericArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected GenericArcSymbolTableCompleter _symbolTableCompleter() {
    return new GenericArcSymbolTableCompleter();
  }

  public static GenericArcSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millGenericArcSymbolTableCompleterDelegator == null) {
      millGenericArcSymbolTableCompleterDelegator = GenericArcMillTOP.getMill();
    }
    return millGenericArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected GenericArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new GenericArcSymbolTableCompleterDelegator();
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new GenericArcCompTypeExprDeSer();
  }

  public static void initMe(GenericArcMill a)  {
    GenericArcMillTOP.initMe(a);
    millGenericArcSymbolTableCompleter = a;
    millGenericArcSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    GenericArcMillTOP.reset();
    millGenericArcSymbolTableCompleter = null;
    millGenericArcSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}