<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTArcParameter ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public <@Util.getTypeString ast.getSymbol().getType()/> ${prefixes.parameter()}${ast.getName()}() {
  return this.${prefixes.parameter()}${ast.getName()};
}