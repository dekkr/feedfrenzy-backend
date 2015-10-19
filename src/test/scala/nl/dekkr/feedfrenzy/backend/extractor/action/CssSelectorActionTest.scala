package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.{CssSelector, Attribute}
import org.scalatest.FlatSpecLike

class CssSelectorActionTest  extends FlatSpecLike {

  val CS = new CssSelectorAction()

  val testHtml =
    """
      <a href="https://www.eff.org/about/staff/maira-sutton">Maira Sutton</a>
    """.stripMargin

  "CssSelectorAction" should "find a pattern" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttribute = new CssSelector(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "a")
    assert(CS.execute(vars, actionAttribute) == List("Maira Sutton"))
  }

  it should "handle a non-matching pattern" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttributeNoMatch = new CssSelector(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "img")
    assert(CS.execute(vars, actionAttributeNoMatch).isEmpty)
  }

  it should "not fail on empty input" in {
    val vars = Map("input" -> List(""))
    val actionAttributeNoMatch = new CssSelector(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "a")
    assert(CS.execute(vars, actionAttributeNoMatch).isEmpty)
  }

  it should "not fail on empty selector" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttributeNoMatch = new CssSelector(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "")
    assert(CS.execute(vars, actionAttributeNoMatch).isEmpty)
  }


}
