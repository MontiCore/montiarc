/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.class2mc

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.Usage.JAVA_API

/**
 * Allows a standard Java API variant to satisfy a Class2MC API request.
 */
class JavaIsValidForClass2MC : AttributeCompatibilityRule<Usage> {

  override fun execute(details: CompatibilityCheckDetails<Usage>) = with(details) {
    if (consumerValue?.name == CLASS2MC_USAGE
      && producerValue?.name == JAVA_API) {
      compatible()
    }
  }
}
