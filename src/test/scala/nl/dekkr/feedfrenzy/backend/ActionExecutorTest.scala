package nl.dekkr.feedfrenzy.backend

import nl.dekkr.feedfrenzy.backend.model.Action
import nl.dekkr.feedfrenzy.backend.util.ActionExecutor
import org.scalatest.FlatSpecLike


class ActionExecutorTest extends FlatSpecLike {

  val AE = new ActionExecutor()

  val actionSplit = Action(order = 1, actionType = "split", inputVariable = None, outputVariable = Option("anchor"), template = Some("div.views-field-view-node > span.field-content > a"), replaceWith = None)

  val actionAttribute = Action(order = 2, actionType = "attribute", inputVariable = Option("anchor"), outputVariable = None, template = Some("href"), replaceWith = None)


  "ActionExecutor" should "succeed on a single split action" in {
    val content = getFileAsString("eff-deeplinks-blog.html")

    val splitted = AE.start(content, List(actionSplit.copy(outputVariable = None)))
    assert(splitted.length == 8)
    splitted.foreach(a => assert(a.startsWith("<a")))
  }

 it should "succeed on a split / attribute sequence" in {
    val content = getFileAsString("eff-deeplinks-blog.html")

    val attributed = AE.start(content, List(actionSplit, actionAttribute))
    assert(attributed.length == 8)
    attributed.foreach(a => assert(a.startsWith("https://")))
  }

  it should "return an empty list on a empty body" in {
    val content = getFileAsString("empty-body.html")

    val attributed = AE.start(content, List(actionSplit, actionAttribute))
    assert(attributed.isEmpty)
  }



  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
