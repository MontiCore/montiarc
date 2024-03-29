/* (c) https://github.com/MontiCore/monticore */
package arccore;

import arccore._symboltable.ArcCoreScopesGenitorP2;
import arccore._symboltable.ArcCoreScopesGenitorP2Delegator;
import de.monticore.types.check.FullCompKindExprDeSer;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class ArcCoreMill extends ArcCoreMillTOP {

  protected static ArcCoreMill millArcCoreScopesGenitorP2;

  protected static ArcCoreMill millArcCoreScopesGenitorP2Delegator;

  protected static ArcCoreMill millArcCoreCompTypeExprDeSer;

  public static ArcCoreScopesGenitorP2 scopesGenitorP2() {
    if (millArcCoreScopesGenitorP2 == null) {
      millArcCoreScopesGenitorP2 = getMill();
    }
    return millArcCoreScopesGenitorP2._scopesGenitorP2();
  }

  protected ArcCoreScopesGenitorP2 _scopesGenitorP2() {
    return new ArcCoreScopesGenitorP2();
  }

  public static ArcCoreScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millArcCoreScopesGenitorP2Delegator == null) {
      millArcCoreScopesGenitorP2Delegator = getMill();
    }
    return millArcCoreScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  protected ArcCoreScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new ArcCoreScopesGenitorP2Delegator();
  }

  public static FullCompKindExprDeSer compTypeExprDeSer() {
    if (millArcCoreCompTypeExprDeSer == null) {
      millArcCoreCompTypeExprDeSer = getMill();
    }
    return millArcCoreCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected FullCompKindExprDeSer _compTypeExprDeSer() {
    return new GenericArcCompTypeExprDeSer();
  }

  public static void initMe(ArcCoreMill a) {
    ArcCoreMillTOP.initMe(a);
    millArcCoreScopesGenitorP2 = a;
    millArcCoreScopesGenitorP2Delegator = a;
    millArcCoreCompTypeExprDeSer = a;
  }

  public static void reset() {
    ArcCoreMillTOP.reset();
    millArcCoreScopesGenitorP2 = null;
    millArcCoreScopesGenitorP2Delegator = null;
    millArcCoreCompTypeExprDeSer = null;
  }
}