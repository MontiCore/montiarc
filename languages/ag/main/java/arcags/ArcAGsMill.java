/* (c) https://github.com/MontiCore/monticore */
package arcags;

import arcags._symboltable.ArcAGScopesGenitorP2;
import arcags._symboltable.ArcAGScopesGenitorP2Delegator;

public class ArcAGsMill extends ArcAGsMillTOP{
  protected static ArcAGsMill millArcAGsScopesGenitorP2;

  protected static ArcAGsMill millArcAGsScopesGenitorP2Delegator;

  public static ArcAGScopesGenitorP2 scopesGenitorP2() {
    if (millArcAGsScopesGenitorP2 == null) {
      millArcAGsScopesGenitorP2 = getMill();
    }
    return millArcAGsScopesGenitorP2._scopesGenitorP2();
  }

  protected ArcAGScopesGenitorP2 _scopesGenitorP2() {
    return new ArcAGScopesGenitorP2();
  }

  public static ArcAGScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millArcAGsScopesGenitorP2Delegator == null) {
      millArcAGsScopesGenitorP2Delegator = getMill();
    }
    return millArcAGsScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }
  protected ArcAGScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new ArcAGScopesGenitorP2Delegator();
  }

  public static void initMe(ArcAGsMill a) {
    ArcAGsMillTOP.initMe(a);
    millArcAGsScopesGenitorP2 = a;
    millArcAGsScopesGenitorP2Delegator = a;
  }

  public static void reset() {
    ArcAGsMillTOP.reset();
    millArcAGsScopesGenitorP2 = null;
    millArcAGsScopesGenitorP2Delegator = null;
  }
}
