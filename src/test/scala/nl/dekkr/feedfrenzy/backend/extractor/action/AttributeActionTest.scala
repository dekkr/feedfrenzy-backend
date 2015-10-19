package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.Attribute
import org.scalatest.FlatSpecLike


class AttributeActionTest extends FlatSpecLike {

  val AA = new AttributeAction()

  val testHtml =
    """
      <a href="https://www.eff.org/about/staff/maira-sutton">Maira Sutton</a>
    """.stripMargin

  "AttributeAction" should "find a attribute" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttribute = new Attribute(inputVariable = Some("input"), outputVariable = Some("split"), attribute = "href")
    assert(AA.execute(vars, actionAttribute) == List("https://www.eff.org/about/staff/maira-sutton"))
  }

  it should "handle a non-exisiting attribute" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttributeNoMatch = new Attribute(inputVariable = Some("input"), outputVariable = Some("split"), attribute = "src")
    assert(AA.execute(vars, actionAttributeNoMatch) == List(""))
  }

  it should "not fail on empty input" in {
    val vars = Map("input" -> List(""))
    val actionAttributeNoMatch = new Attribute(inputVariable = Some("input"), outputVariable = Some("split"), attribute = "href")
    assert(AA.execute(vars, actionAttributeNoMatch) == List(""))
  }

  it should "not fail on empty attribute" in {
    val vars = Map("input" -> List(testHtml))
    val actionAttributeNoMatch = new Attribute(inputVariable = Some("input"), outputVariable = Some("split"), attribute = "")
    assert(AA.execute(vars, actionAttributeNoMatch) == List(""))
  }


}
