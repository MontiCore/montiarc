<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>
<#assign existenceConditions = helper.getExistenceCondition(ast, field)/>

@Override
public void ${prefixes.setterMethod()}${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)}(<@Util.getTypeString field.getType()/> value) {
  this.${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)} = value;
  <@Log.info log_aspects.fieldValue() "getName()">
    "${field.getName()} = " + montiarc.rte.logging.DataFormatter.format(this.${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)})
  </@Log.info>
}