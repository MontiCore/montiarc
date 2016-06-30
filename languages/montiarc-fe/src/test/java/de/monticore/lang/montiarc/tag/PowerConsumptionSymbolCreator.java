//package de.monticore.lang.montiarc.tag;
//
//import java.util.Optional;
//
//import de.monticore.lang.montiarc.montiarc._symboltable.ExpandedComponentInstanceSymbol;
//import de.monticore.lang.montiarc.tagging._ast.ASTNameScope;
//import de.monticore.lang.montiarc.tagging._ast.ASTScope;
//import de.monticore.lang.montiarc.tagging._ast.ASTTag;
//import de.monticore.lang.montiarc.tagging._ast.ASTTagElement;
//import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
//import de.monticore.lang.montiarc.tagging._ast.ASTTargetElement;
//import de.monticore.lang.montiarc.tagging._ast.ASTUnitTagValue;
//import de.monticore.lang.montiarc.tagging._ast.ASTValuedTag;
//import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
//import de.monticore.lang.montiarc.unit.Unit;
//import de.monticore.lang.montiarc.unit.Units;
//import de.monticore.literals.literals._ast.ASTDoubleLiteral;
//import de.monticore.literals.literals._ast.ASTFloatLiteral;
//import de.monticore.literals.literals._ast.ASTIntLiteral;
//import de.monticore.literals.literals._ast.ASTLongLiteral;
//import de.monticore.literals.literals._ast.ASTNumericLiteral;
//import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
//import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
//import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
//import de.monticore.literals.literals._ast.ASTSignedLongLiteral;
//import de.monticore.symboltable.Scope;
//import de.se_rwth.commons.Joiners;
//import de.se_rwth.commons.logging.Log;
//
///**
// * Created by Michael von Wenckstern on 31.05.2016.
// * only for this tests --> this should be generated using the tag schema
// */
//public class PowerConsumptionSymbolCreator implements TagSymbolCreator {
//  // TODO ASTNumericLiteral should have a getValue() method
//  protected static double getValue(ASTNumericLiteral numericLiteral) {
//    if (numericLiteral instanceof ASTDoubleLiteral) {
//      return ((ASTDoubleLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTSignedDoubleLiteral) {
//      return ((ASTSignedDoubleLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTIntLiteral) {
//      return ((ASTIntLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTSignedIntLiteral) {
//      return ((ASTSignedIntLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTFloatLiteral) {
//      return ((ASTFloatLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTSignedFloatLiteral) {
//      return ((ASTSignedFloatLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTLongLiteral) {
//      return ((ASTLongLiteral) numericLiteral).getValue();
//    }
//    else if (numericLiteral instanceof ASTSignedLongLiteral) {
//      return ((ASTSignedLongLiteral) numericLiteral).getValue();
//    }
//    else {
//      throw new Error("unexpected ASTNumericLiteral: " + numericLiteral.getClass());
//    }
//  }
//
//  public static Scope getGlobalScope(final Scope scope) {
//    Scope s = scope;
//    while (s.getEnclosingScope().isPresent()) {
//      s = s.getEnclosingScope().get();
//    }
//    return s;
//  }
//
//  public void create(ASTTaggingUnit unit, Scope gs) {
//    if (unit.getQualifiedNames().stream()
//        .map(q -> q.toString())
//        .filter(n -> n.endsWith("PowerConsumptionTagSchema"))
//        .count() == 0) {
//      return; // the tagging model is not conform to the traceability tagging schema
//    }
//    final String packageName = Joiners.DOT.join(unit.getPackage());
//    final String rootCmp = // if-else does not work b/c of final (required by streams)
//        (unit.getTagBody().getTargetModel().isPresent()) ?
//            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModel().get())
//                .getQualifiedName().toString()) :
//            packageName;
//
//    for (final ASTTagElement element : unit.getTagBody().getTagElements()) {
//      if (element instanceof ASTTargetElement) {
//        ((ASTTargetElement) element).getTags().stream()
//            .filter(t -> t.getName().equals("PowerConsumption"))
//            .filter(this::checkASTTagKind)
//            .map(t -> (ASTValuedTag) t)
//            .filter(this::checkValueKind)
//            .map(v -> (ASTUnitTagValue) v.getTagValue())
//            .filter(this::checkUnitKind)
//            .forEachOrdered(v ->
//                ((ASTTargetElement) element).getScopes().stream()
//                    .filter(this::checkScope)
//                    .map(s -> (ASTNameScope) s)
//                    .map(s -> getGlobalScope(gs).<ExpandedComponentInstanceSymbol>
//                        resolveDown(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
//                        s.getQualifiedName().toString()), ExpandedComponentInstanceSymbol.KIND))
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .forEachOrdered(s -> s.addTag(new PowerConsumptionSymbol(getValue(v.getNumericLiteral()),
//                        Units.getPower(v.getUnit()).get())))
//            );
//      }
//    }
//  }
//
//  protected boolean checkUnitKind(ASTUnitTagValue unit) {
//    if (Units.getPower(unit.getUnit()).isPresent()) {
//      return true;
//    }
//    Optional<Unit> u = Units.getUnit(unit.getUnit());
//    if(u.isPresent()) {
//      Log.error(String.format("0xTEST1 Invalid unit kind: '%s' of unit '%s'. ComponentLayout expects as unit kind 'Power'.",
//          u.get().getKind(), u.get().toString()), unit.get_SourcePositionStart(), Units.getAvailablePowerUnits());
//      return false;
//    }
//    Log.error(String.format("0xTEST2 Unit is unknown: '%s'.", unit.getUnit()), unit.get_SourcePositionStart());
//    return false;
//  }
//
//  protected boolean checkASTTagKind(ASTTag tag) {
//    if (tag.getTagKind().equals("ValuedTag")) {
//      return true;
//    }
//    Log.error(String.format("0xTEST3 Invalid tag kind: '%s'. ComponentLayout expects as tag kind 'ValuedTag'.",
//        tag.getTagKind()), tag.get_SourcePositionStart());
//    return false;
//  }
//
//  protected boolean checkValueKind(ASTValuedTag tag) {
//    if (tag.getTagValue() != null &&
//        tag.getTagValue().getValueKind().equals("UnitValue")) {
//      return true;
//    }
//    Log.error(String.format("0xTEST4 Invalid value kind: '%s'. ComponentLayout expects as tag kind 'UnitValue'.",
//        tag.getTagValue() != null ? tag.getTagValue().getValueKind() : "tag value is zero"),
//        tag.get_SourcePositionStart());
//    return false;
//  }
//
//  protected boolean checkScope(ASTScope scope) {
//    if (scope.getScopeKind().equals("NameScope")) {
//      return true;
//    }
//    Log.error(String.format("0xTEST5 Invalid scope kind: '%s'. ComponentLayout expects as scope kind 'NameScope'.",
//        scope.getScopeKind()), scope.get_SourcePositionStart());
//    return false;
//  }
//}
