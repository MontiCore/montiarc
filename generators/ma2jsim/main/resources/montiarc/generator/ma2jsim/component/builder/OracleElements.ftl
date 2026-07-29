<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- @ftlvariable name="className" type="String" -->
${tc.signature("className")}
protected montiarc.rte.oracle.OracleFactory oracleFactory = montiarc.rte.oracle.OracleFactory.withDefaultStrategy(${helper.getComponentHelper().getOracleFactory(ast)});

public ${className} setOracleFactory(montiarc.rte.oracle.OracleFactory oracleFactory) { this.oracleFactory = oracleFactory; return this; }

public montiarc.rte.oracle.OracleFactory getOracleFactory() { return this.oracleFactory; }
