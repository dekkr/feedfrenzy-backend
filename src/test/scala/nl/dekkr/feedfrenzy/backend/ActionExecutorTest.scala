package nl.dekkr.feedfrenzy.backend

import nl.dekkr.feedfrenzy.backend.model.Action
import nl.dekkr.feedfrenzy.backend.util.ActionExecutor
import org.scalatest.FlatSpecLike


class ActionExecutorTest extends FlatSpecLike {

  val AE = new ActionExecutor()

  val actionSplit = Action(order = 1, actionType = "split", inputVariable = None, outputVariable = Option("anchor"), template = Some("li.tour-of-scala > a"), replaceWith = None)
  val actionSplitDA = Action(order = 1, actionType = "split", inputVariable = None, outputVariable = Option("anchor"), template = Some("div.frame_buy > a"), replaceWith = None)

  val actionAttribute = Action(order = 2, actionType = "attribute", inputVariable = Option("anchor"), outputVariable = None, template = Some("href"), replaceWith = None)


  "ActionExecutor" should "succeed on a single split action" in {
    val content = getFileAsString("doc.scala-lang.org.html")

    val splitted = AE.start(content, List(actionSplit.copy(outputVariable = None)))
    assert(splitted.length == 33)
    splitted.foreach(a => assert(a.startsWith("<a")))
  }

 it should "succeed on a split / attribute sequence" in {
    val content = getFileAsString("doc.scala-lang.org.html")

    val attributed = AE.start(content, List(actionSplit, actionAttribute))
    assert(attributed.length == 33)
    attributed.foreach(a => assert(a.startsWith("http://")))
  }

  it should "return an empty list on a empty body" in {
    val content = getFileAsString("empty-body.html")

    val attributed = AE.start(content, List(actionSplit, actionAttribute))
    assert(attributed.isEmpty)
  }

  it should "succeed on a split / attribute sequence for DA" in {
    val content = getFileAsString("dagartikel.html")

    val attributed = AE.start(content, List(actionSplitDA, actionAttribute))
    assert(attributed.length == 128)
    attributed.foreach(println)
    attributed.foreach(a => assert(a.startsWith("http://")))
  }


  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
