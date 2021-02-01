/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class TemplateHelper {
  
  public boolean hasConstructors(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    return typeSymbol.getMethodSignatureList().stream()
      .anyMatch(MethodSymbolTOP::isIsConstructor);
  }
  
  public Collection<String> getThrowsDeclarationEntries(@NotNull CDMethodSignatureSymbol methodSignature) {
    Preconditions.checkNotNull(methodSignature);
    if (!methodSignature.getExceptionsList().isEmpty()) {
      return methodSignature.getExceptionsList().stream()
        .map(exp -> exp.getTypeInfo().getName()).collect(Collectors.toList());
    } else if (methodSignature.isPresentAstNode()) {
      if (methodSignature.getAstNode() instanceof ASTCDMethod) {
        ASTCDMethod astcdMethod = (ASTCDMethod) methodSignature.getAstNode();
        if (astcdMethod.isPresentCDThrowsDeclaration()) {
          return astcdMethod.getCDThrowsDeclaration().getExceptionList().stream().map(ASTMCQualifiedName::toString).collect(Collectors.toList());
        } else {
          return new ArrayList<>();
        }
      } else {
        return new ArrayList<>();
      }
    } else {
      return new ArrayList<>();
    }
  }
  
  public Collection<Attribute> getAttributes(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    Collection<Attribute> res = new ArrayList<>();
    //Attributes come from attribute definitions and associations
    for (FieldSymbol fieldSymbol : typeSymbol.getFieldList()) {
      res.add(new Attribute(
        getModifiers(fieldSymbol),
        fieldSymbol.getType().getTypeInfo().getName(),
        fieldSymbol.getName(),
        fieldSymbol.isIsReadOnly()));
    }
    for (CDRoleSymbol roleSymbol : typeSymbol.getCDRoleList()) {
      if (roleSymbol.isIsDefinitiveNavigable()) {
        res.add(new Attribute(
          getModifiers(roleSymbol), getType(roleSymbol),
          roleSymbol.getName(), roleSymbol.isIsReadOnly()));
      }
    }
    return res;
  }
  
  public Collection<Field> getConstructorParams(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    Collection<Field> res = new ArrayList<>();
    //Attributes come from attribute definitions and associations
    //But we ignore associations where the cardinality can be 0 for the constructor
    for (FieldSymbol fieldSymbol : typeSymbol.getFieldList()) {
      res.add(new Field(fieldSymbol.getType().getTypeInfo().getName(),
        fieldSymbol.getName()));
    }
    for (CDRoleSymbol roleSymbol : typeSymbol.getCDRoleList()) {
      if (roleSymbol.isIsReadOnly() ||
        (roleSymbol.isIsDefinitiveNavigable()
          && (!roleSymbol.isPresentCardinality() || roleSymbol.getCardinality().isAtLeastOne()))) {
        res.add(new Field(getType(roleSymbol), roleSymbol.getName()));
      }
    }
    return res;
  }
  
  protected String getModifiers(@NotNull CDRoleSymbol roleSymbol) {
    Preconditions.checkNotNull(roleSymbol);
    return getModifiers((FieldSymbol) roleSymbol);
  }
  
  protected String getModifiers(@NotNull FieldSymbol fieldSymbol) {
    Preconditions.checkNotNull(fieldSymbol);
    String res = "";
    if (fieldSymbol.isIsPrivate()) {
      res += "private ";
    } else if (fieldSymbol.isIsProtected()) {
      res += "protected ";
    } else if (fieldSymbol.isIsPublic()) {
      res += "public ";
    }
    if (fieldSymbol.isIsStatic()) {
      res += "static ";
    }
    if (fieldSymbol.isIsFinal()) {
      res += "final ";
    }
    return res;
  }
  
  protected String getType(@NotNull CDRoleSymbol roleSymbol) {
    Preconditions.checkNotNull(roleSymbol);
    String type = "";
    if (!roleSymbol.isPresentCardinality() || roleSymbol.getCardinality().isOne()) {
      type = roleSymbol.getType().getTypeInfo().getName();
    } else if (roleSymbol.getCardinality().isOpt()) {
      type = "Optional<" + roleSymbol.getType().getTypeInfo().getName() + ">";
    } else if (roleSymbol.isPresentAttributeQualifier()) {
      type = "Map<" + roleSymbol.getAttributeQualifier().getType().getTypeInfo().getName() +
        ", " + roleSymbol.getType().getTypeInfo().getName() + ">";
    } else if (roleSymbol.isPresentTypeQualifier()) {
      type = "Map<" + roleSymbol.getTypeQualifier().getTypeInfo().getName() +
        ", " + roleSymbol.getType().getTypeInfo().getName() + ">";
    } else {
      type = "List<" + roleSymbol.getType().getTypeInfo().getName() + ">";
    }
    return type;
  }
  
  public static class Field {
    String type, name;
    
    public Field(String type, String name) {
      this.type = type;
      this.name = name;
    }
    
    public String getType() {
      return this.type;
    }
    
    public String getName() {
      return this.name;
    }
    
  }
  
  public static class Attribute extends Field {
    Boolean readOnly;
    String modifier;
    
    public Attribute(String modifier, String type, String name, Boolean readOnly) {
      super(type, name);
      this.readOnly = readOnly;
      this.modifier = modifier;
    }
    
    public Boolean isReadOnly() {
      return this.readOnly;
    }
    
    public String getModifier() {
      return this.modifier;
    }
  }
  
  public Collection<CDMethodSignatureSymbol> getCDMethodsFromImplementedInterfaces(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    if (typeSymbol.isIsInterface()) {
      return new ArrayList<>();
    }
    return typeSymbol.getInterfaceList().stream()
      .map(SymTypeExpression::getTypeInfo)
      .flatMap(ooTypeSymbol -> ooTypeSymbol.getFunctionList().stream())
      .filter(functionSymbol -> functionSymbol instanceof CDMethodSignatureSymbol)
      .filter(functionSymbol -> typeSymbol.getMethodSignatureList().stream()
        .noneMatch(otherSignature -> methodSignaturesMatch(functionSymbol, otherSignature)))
      .map(methodSymbol -> (CDMethodSignatureSymbol) methodSymbol)
      .collect(Collectors.toList());
  }
  
  public Collection<FunctionSymbol> getNonCDMethodsFromImplementedInterfaces(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    if (typeSymbol.isIsInterface()) {
      return new ArrayList<>();
    }
    return typeSymbol.getInterfaceList().stream()
      .map(SymTypeExpression::getTypeInfo)
      .flatMap(ooTypeSymbol -> ooTypeSymbol.getFunctionList().stream())
      .filter(functionSymbol -> !(functionSymbol instanceof CDMethodSignatureSymbol))
      .filter(functionSymbol -> typeSymbol.getMethodSignatureList().stream()
        .noneMatch(otherSignature -> methodSignaturesMatch(functionSymbol, otherSignature)))
      .collect(Collectors.toList());
  }
  
  protected static boolean methodSignaturesMatch(@NotNull FunctionSymbol _this, CDMethodSignatureSymbol other) {
    Preconditions.checkNotNull(_this);
    Preconditions.checkNotNull(other);
    /*
    What is happening here:
    1) check that the methods have the same name
    if yes:
    2) check that the methods have the same number of parameters
    if yes:
    3) check that the methods have the same return type
    if yes:
    4) check that the type of every parameter of one method is the type of a parameter of the other method
     */
    return (_this.getName().equals(other.getName()))
      && (_this.getParameterList().size() == other.getParameterList().size())
      && (_this.getReturnType().deepEquals(other.getReturnType()))
      && (!_this.getParameterList().stream().allMatch(
      thisParam -> other.getParameterList().stream().anyMatch(
        otherParam -> thisParam.getType().deepEquals(otherParam.getType()))));
    
  }
}