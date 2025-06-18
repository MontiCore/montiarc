<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("className")}
protected montiarc.rte.component.SimComponent superComponent;

public ${className} setSuperComponent(montiarc.rte.component.SimComponent superComponent) { this.superComponent = superComponent; return this; }

public montiarc.rte.component.SimComponent getSuperComponent() { return this.superComponent; }
