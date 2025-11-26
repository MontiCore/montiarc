/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.cd;

import com.microsoft.z3.Constructor;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import montiarc.conformance.util.SMTAutomataUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static montiarc.conformance.util.SMTAutomataUtils.mkConstructor;

public class CD2SMT {
  private final Context ctx;
  private final Map<FieldSymbol, Constructor<?>> enumConstantMap = new LinkedHashMap<>();
  private final Map<ASTCDEnum, Sort> enumSortMap = new LinkedHashMap<>();
  private final ASTCDCompilationUnit ast;

  public CD2SMT(ASTCDCompilationUnit cd, Function<String, String> ident, Context ctx) {
    this.ctx = ctx;
    this.ast = cd;

    // transform enum to SMT
    if (cd != null) {
      cd.getCDDefinition().getCDEnumsList().forEach(astcdEnum -> enum2smt(astcdEnum, ident));
    }
  }

  private void enum2smt(ASTCDEnum astcdEnum, Function<String, String> ident) {
    List<Constructor<?>> constructors = new ArrayList<>();

    // create constructor for each enum const
    for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
      Constructor<?> constructor = mkConstructor(ident.apply(constant.getName()), ctx);
      constructors.add(constructor);
      enumConstantMap.put(constant.getSymbol(), constructor);
    }

    // make a sort form this constructors
    String sortName = ident.apply(astcdEnum.getName());
    Sort sort = SMTAutomataUtils.mkDatatype(sortName, constructors, ctx);
    enumSortMap.put(astcdEnum, sort);
  }

  public Expr<?> getConst(FieldSymbol enumConst) {
    return ctx.mkConst(enumConstantMap.get(enumConst).ConstructorDecl());
  }

  public Sort getSort(ASTCDEnum astcdEnum) {
    return enumSortMap.get(astcdEnum);
  }

  public ASTCDCompilationUnit getAst() {
    return ast;
  }
}
