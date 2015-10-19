package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.CssSelector
import org.scalatest.FlatSpecLike


class CssRemoveActionTest extends FlatSpecLike {

  val RWCS = new CssRemoveAction()

  val testHtml =
    """
      |October 5, 2015 | By <a href="https://www.eff.org/about/staff/maira-sutton">Maira Sutton</a>
    """.stripMargin

  val vars = Map("input" -> List(testHtml))

  val actionRemove = new CssSelector(inputVariable = Some("input"), outputVariable = Some("split"), selectorPattern = "a")
  "RemoveWithCssSelectorAction" should "remove the achor tag" in {
    assert(RWCS.execute(vars, actionRemove) == List("October 5, 2015 | By \n"))
  }

  it should "not touch the input, since the selector can not be found" in {
    val actionRemoveNoMatch = actionRemove.copy(selectorPattern = "img")
    assert(RWCS.execute(vars, actionRemoveNoMatch) == List(testHtml))
  }

  it should "return an empty string for when the input is empty" in {
    assert(RWCS.execute(Map("input" -> List("")), actionRemove) == List(""))
    assert(RWCS.execute(Map("input" -> List.empty), actionRemove) == List.empty)
  }


}