/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.fmu2arc.FMU2ARC_API_FILE_USAGE
import montiarc.gradle.fmu2arc.FMU2ARC_API_SYMBOL_USAGE
import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.Usage

/**
 * Using this rule, dependency variants with the [Usage] attribute values [FMU2ARC_API_SYMBOL_USAGE] and [FMU2ARC_API_FILE_USAGE] are compatible when
 * we actually search for an [Usage] attribute value of [FMU2ARC_4_MONTIARC_USAGE].
 */
class FMU2ArcIsValidForMontiArc : AttributeCompatibilityRule<Usage> {
  override fun execute(t: CompatibilityCheckDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == FMU2ARC_4_MONTIARC_USAGE
      && producerValue != null && (
          producerValue!!.name == FMU2ARC_4_MONTIARC_USAGE || producerValue!!.name == FMU2ARC_API_SYMBOL_USAGE || producerValue!!.name == FMU2ARC_API_FILE_USAGE)
    ) {
      compatible()
    }
  }
}
