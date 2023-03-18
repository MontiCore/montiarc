/* (c) https://github.com/MontiCore/monticore */
package arccore;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import arccore._symboltable.ArcCoreSymbolTableCompleter;
import arccore._symboltable.ArcCoreSymbolTableCompleterDelegator;
import de.monticore.prettyprint.IndentPrinter;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class ArcCoreMill extends ArcCoreMillTOP {

  protected static ArcCoreMill millArcCoreSymbolTableCompleter ;

  protected static ArcCoreMill millArcCoreSymbolTableCompleterDelegator;

  protected static ArcCoreMill millFullPrettyPrinter;

  protected static ArcCoreMill millCompTypeExprDeSer;
  
  public static ArcCoreSymbolTableCompleter symbolTableCompleter ()  {
    if (millArcCoreSymbolTableCompleter == null) {
      millArcCoreSymbolTableCompleter = getMill();
    }
    return millArcCoreSymbolTableCompleter._symbolTableCompleter();
  }

  protected ArcCoreSymbolTableCompleter _symbolTableCompleter() {
    return new ArcCoreSymbolTableCompleter();
  }

  public static ArcCoreSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millArcCoreSymbolTableCompleterDelegator == null) {
      millArcCoreSymbolTableCompleterDelegator = getMill();
    }
    return millArcCoreSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected ArcCoreSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new ArcCoreSymbolTableCompleterDelegator();
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

  public static void initMe(ArcCoreMill a)  {
    ArcCoreMillTOP.initMe(a);
    millArcCoreSymbolTableCompleter = a;
    millArcCoreSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    ArcCoreMillTOP.reset();
    millArcCoreSymbolTableCompleter = null;
    millArcCoreSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}