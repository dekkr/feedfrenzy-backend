package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.{Regex, Split}
import org.scalatest.FlatSpecLike

class RegexTest extends FlatSpecLike {

  val RA = new RegexAction()

  val testHtml =
    """
      |October 5, 2015 | By <a href="https://www.eff.org/about/staff/maira-sutton">Maira Sutton</a>
    """.stripMargin
  val vars = Map("input" -> List(testHtml))

  val actionSplit = new Regex(inputVariable = Some("input"), outputVariable = Some("split"), regex = "(\\w+)\\s(\\d+),\\s(\\d+)")
  "RegexAction" should "find a date" in {
    assert(RA.execute(vars, actionSplit) == List("October 5, 2015"))
  }

  it should "return a empty string for unmatched patterns" in {
    val actionSplitNoMatch = actionSplit.copy(regex = "(\\w+)\\s(\\d+)\\s(\\d+)")
    assert(RA.execute(vars, actionSplitNoMatch) == List(""))
  }

}
