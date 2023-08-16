<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTArcField ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected <@Util.getTypeString ast.getSymbol().getType()/> ${prefixes.field()}${ast.getName()} = ${prettyPrinter.prettyprint(ast.getInitial())};
