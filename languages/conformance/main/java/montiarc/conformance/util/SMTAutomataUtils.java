/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class SMTAutomataUtils {
  public static String CON_SUFFIX = "_con";
  public static String REF_SUFFIX = "_ref";
  public static String STATE_ACCESSOR = "get_state";
  public static String ACCESSOR_PREFIX = "get_";

  public static int varCounter = 0;

  public static String mkAccessorName(PortSymbol portSymbol) {
    return ACCESSOR_PREFIX + portSymbol.getName();
  }

  public static String mkAccessorName(VariableSymbol var) {
    return ACCESSOR_PREFIX + var.getName();
  }

  public static Sort getSMTSort(TypeSymbol type, CD2SMT cd2SMT, Context ctx) {

    // case port type is a simple smt type
    String typeName = type.getName();
    if (hasSupportedSMTSort(type)) {
      return typeMap(ctx).get(typeName);
    }

    Optional<ASTCDEnum> astCdEnum = SymbolTableUtil.resolveEnum(type, cd2SMT.getAst());
    if (astCdEnum.isPresent()) {
      return cd2SMT.getSort(astCdEnum.get());
    }

    Log.error("Type " + typeName + " Not yet Supported for ports");
    assert false;
    return null;
  }

  public static boolean hasSupportedSMTSort(TypeSymbol symbol) {
    return Set.of("String", "int", "double", "Double", "Integer", "boolean")
        .contains(symbol.getName());
  }

  public static DatatypeSort<? extends Sort> mkDatatype(
      String name, List<Constructor<?>> constructors, Context ctx) {
    return ctx.mkDatatypeSort(name, constructors.toArray(new Constructor[0]));
  }

  public static Constructor<Sort> mkConstructor(String name, Context ctx) {
    return ctx.mkConstructor(name, "is_" + name, null, null, null);
  }

  public static Constructor<Sort> mkConstructor(
      String name, List<String> fieldsNames, List<Sort> sorts, Context ctx) {

    return ctx.mkConstructor(
        name, "is_" + name, fieldsNames.toArray(new String[0]), sorts.toArray(new Sort[0]), null);
  }

  public static Map<String, Sort> typeMap(Context ctx) {
    Map<String, Sort> typeMap = new HashMap<>();
    typeMap.put("Boolean", ctx.mkBoolSort());
    typeMap.put("Double", ctx.getRealSort());
    typeMap.put("Integer", ctx.mkIntSort());
    typeMap.put("String", ctx.mkStringSort());
    typeMap.put("boolean", ctx.mkBoolSort());
    typeMap.put("double", ctx.getRealSort());
    typeMap.put("int", ctx.mkIntSort());
    return typeMap;
  }

  public static String mkSymbol(String name) {
    return name + "_" + varCounter++;
  }
}
