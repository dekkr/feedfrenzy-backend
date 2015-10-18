package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.{Split, DateParser}
import org.scalatest.FlatSpecLike


class SplitActionTest extends FlatSpecLike {

  val SA = new SplitAction()

  val testHtml =
    """
      <div>
         <div><a href="#1">1</a></div>
         <div><a href="#2">2</a></div>
         <div><a href="#3">3</a></div>
         <div><a href="#4">4</a></div>
      </div>
    """.stripMargin

  "SplitAction" should "should create a list of matches" in {
    val vars = Map("input" -> List(testHtml))

    val actionSplit = new Split(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "div > a")
    assert(SA.execute(vars, actionSplit) == List(
      "<a href=\"#1\">1</a>",
      "<a href=\"#2\">2</a>",
      "<a href=\"#3\">3</a>",
      "<a href=\"#4\">4</a>")
    )
  }

  it should "should handle empty content and pattern" in {

    val vars = Map("input" -> List(testHtml))

    val actionSplitNoPattern = new Split(inputVariable = Some("input"), outputVariable = Some("split"),selectorPattern = "")
    assert(SA.execute(vars, actionSplitNoPattern).isEmpty)

    val actionSplitNoContent = new Split(inputVariable = None, outputVariable = Some("split"),selectorPattern = "div > a")
    assert(SA.execute(vars, actionSplitNoContent).isEmpty)
  }

}
