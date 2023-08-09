/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.cd2pojo.CD2POJO_API_SYMBOL_USAGE
import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.Usage

/**
 * Using this rule, dependency variants with the [Usage] attribute value [CD2POJO_API_SYMBOL_USAGE] are compatible when
 * we actually search for an [Usage] attribute value of [CD2POJO_4_MONTIARC_USAGE].
 */
class Cd2PojoIsValidForMontiArc : AttributeCompatibilityRule<Usage> {
  override fun execute(t: CompatibilityCheckDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == CD2POJO_4_MONTIARC_USAGE
      && producerValue != null && (
        producerValue!!.name == CD2POJO_4_MONTIARC_USAGE || producerValue!!.name == CD2POJO_API_SYMBOL_USAGE)
    ) {
      compatible()
    }
  }
}
