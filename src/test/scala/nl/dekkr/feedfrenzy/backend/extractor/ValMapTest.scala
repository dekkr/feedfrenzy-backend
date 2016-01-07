package nl.dekkr.feedfrenzy.backend.extractor

import org.scalatest.{Matchers, WordSpec}

class ValMapTest extends  WordSpec with ValMap with Matchers {

"ValMap" should {
  val valMap : VariableMap = Map("key1" -> List("value1","value2"))
  val valMapEmptyValues : VariableMap = Map("key1" -> List.empty[String])
  val template = "{key1}"

  "replace keys in a template" in {
    this.replaceVarsInTemplate(template, valMap) shouldBe "value1"
  }

  "remove keys in a template if it has no value" in {
    this.replaceVarsInTemplate(template, valMapEmptyValues) shouldBe ""
  }



}


}
