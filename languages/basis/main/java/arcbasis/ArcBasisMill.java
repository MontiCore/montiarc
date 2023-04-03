/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import arcbasis._symboltable.ArcBasisSymbolTablePass3;
import arcbasis._symboltable.ArcBasisSymbolTablePass3Delegator;
import arcbasis.check.deser.ArcBasisCompTypeExprDeSer;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;

public class ArcBasisMill extends ArcBasisMillTOP {

  protected static ArcBasisMill millArcBasisSymbolTableCompleter;

  protected static ArcBasisMill millArcBasisSymbolTableCompleterDelegator;

  protected static ArcBasisMill millArcBasisSymbolTablePass3;

  protected static ArcBasisMill millArcBasisSymbolTablePass3Delegator;

  protected static ArcBasisMill millFullPrettyPrinter;

  protected static ArcBasisMill millCompTypeExprDeSer;

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

  public static ArcBasisSymbolTablePass3 symbolTablePass3()  {
    if (millArcBasisSymbolTablePass3 == null) {
      millArcBasisSymbolTablePass3 = getMill();
    }
    return millArcBasisSymbolTablePass3._symbolTablePass3();
  }

  protected ArcBasisSymbolTablePass3 _symbolTablePass3() {
    return new ArcBasisSymbolTablePass3();
  }

  public static ArcBasisSymbolTablePass3Delegator symbolTablePass3Delegator()  {
    if (millArcBasisSymbolTablePass3Delegator == null) {
      millArcBasisSymbolTablePass3Delegator = getMill();
    }
    return millArcBasisSymbolTablePass3Delegator._symbolTablePass3Delegator();
  }

  protected ArcBasisSymbolTablePass3Delegator _symbolTablePass3Delegator() {
    return new ArcBasisSymbolTablePass3Delegator();
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new ArcBasisCompTypeExprDeSer();
  }

  public static void initMe(ArcBasisMill a)  {
    ArcBasisMillTOP.initMe(a);
    millArcBasisSymbolTableCompleter = a;
    millArcBasisSymbolTableCompleterDelegator = a;
    millArcBasisSymbolTablePass3 = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    ArcBasisMillTOP.reset();
    millArcBasisSymbolTableCompleter = null;
    millArcBasisSymbolTableCompleterDelegator = null;
    millArcBasisSymbolTablePass3 = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}