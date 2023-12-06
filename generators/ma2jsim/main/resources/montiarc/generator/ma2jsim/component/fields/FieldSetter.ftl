<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTArcField ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public void ${prefixes.setterMethod()}${prefixes.field()}${ast.getName()}(<@Util.getTypeString ast.getSymbol().getType()/> value) {
  this.${prefixes.field()}${ast.getName()} = value;
}