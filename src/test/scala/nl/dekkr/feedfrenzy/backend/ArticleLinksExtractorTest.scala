package nl.dekkr.feedfrenzy.backend

import nl.dekkr.feedfrenzy.backend.model.Action
import nl.dekkr.feedfrenzy.backend.util.ArticleLinksExtractor
import org.scalatest.FlatSpecLike


class ArticleLinksExtractorTest extends FlatSpecLike {

  val AE = new ArticleLinksExtractor()

  val actionSplit = Action(order = 1, actionType = "split", inputVariable = None, outputVariable = Option("anchor"), template = Some("div.views-field-view-node > span.field-content > a"), replaceWith = None)

  val actionAttribute = Action(order = 2, actionType = "attribute", inputVariable = Option("anchor"), outputVariable = None, template = Some("href"), replaceWith = None)


  "AticleLinksExtractor" should "succeed on a single split action" in {
    val content = getFileAsString("eff-deeplinks-blog.html")

    val splitted = AE.getList(content, List(actionSplit.copy(outputVariable = None)))
    assert(splitted.urls.length == 8)
    splitted.urls.foreach(a => assert(a.startsWith("<a")))
  }

  it should "succeed on a split / attribute sequence" in {
    val content = getFileAsString("eff-deeplinks-blog.html")

    val attributed = AE.getList(content, List(actionSplit, actionAttribute))
    assert(attributed.urls.length == 8)
    attributed.urls.foreach(a => assert(a.startsWith("https://")))
  }

  it should "return an empty list on a empty body" in {
    val content = getFileAsString("empty-body.html")

    val attributed = AE.getList(content, List(actionSplit, actionAttribute))
    assert(attributed.urls.isEmpty)
  }


  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
