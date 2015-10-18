package nl.dekkr.feedfrenzy.backend.extractor

import nl.dekkr.feedfrenzy.backend.model.Action
import org.scalatest.FlatSpecLike


class ArticleLinksExtractorTest extends FlatSpecLike {

  val AE = new ArticleLinksExtractor()

  val actionSplit = Action(order = 1, actionType = "split", inputVariable = None, outputVariable = Option("anchor"), template = Some("div.views-field-view-node > span.field-content > a"), replaceWith = None, locale = None, pattern = None, padTime = None)

  val actionAttribute = Action(order = 2, actionType = "attribute", inputVariable = Option("anchor"), outputVariable = None, template = Some("href"), replaceWith = None, locale = None, pattern = None, padTime = None)


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



  it should "return the raw results" in {
    val content = getFileAsString("eff-deeplinks-blog.html")

    val attributed = AE.getRaw(content, List(actionSplit, actionAttribute))
    assert(attributed.variables.length == 1)

    val urls = attributed .variables.find(v => v.name == "anchor").map(_.values).get
    assert(urls.length == 8)
    urls.foreach(a => a.startsWith("<a href=\"https://"))
  }


  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
