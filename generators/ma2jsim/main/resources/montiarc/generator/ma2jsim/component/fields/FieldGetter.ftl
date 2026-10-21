<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}() {
  return this.${prefixes.field()}${field.getName()};
}
