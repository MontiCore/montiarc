/* (c) https://github.com/MontiCore/monticore */
package scmapping.util;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.ocl2smt.ocl2smt.expr2smt.ExprKind;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeFactory;
import de.monticore.ocl2smt.ocl2smt.expr2smt.typeAdapter.TypeAdapter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.function.Function;

public class SCZ3TypeFactory extends Z3TypeFactory {
  protected Function<ASTCDEnum, Sort> getSort;
  protected ASTCDCompilationUnit cd;

  public SCZ3TypeFactory(ASTCDCompilationUnit cd, Function<ASTCDEnum, Sort> getSort, Context ctx) {
    super(ctx);
    this.getSort = getSort;
    this.cd = cd;
  }

  @Override
  public TypeAdapter adapt(SymTypeExpression type) {
    // case primitive type
    Optional<Z3TypeAdapter> res = adaptQName(type.printFullName());

    // case CDType
    if (res.isEmpty()) {
      String[] parts = type.print().split("\\.");
      String typeName = parts[parts.length - 1];
      Optional<ASTCDType> astcdType =
          Optional.ofNullable(CDHelper.getASTCDType(typeName, cd.getCDDefinition()));
      if (astcdType.isPresent() && astcdType.get() instanceof ASTCDEnum) {
        Sort sort = getSort.apply((ASTCDEnum) astcdType.get());
        res = Optional.ofNullable(mkType(astcdType.get().getName(), sort, ExprKind.ENUM));
      }
    }

    if (res.isEmpty()) {
      Log.error("Cannot resolve the type " + type.printFullName());
      assert false;
    }
    return res.get();
  }
}
