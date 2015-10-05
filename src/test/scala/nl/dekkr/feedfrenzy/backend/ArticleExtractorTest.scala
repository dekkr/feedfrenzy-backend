package nl.dekkr.feedfrenzy.backend

import nl.dekkr.feedfrenzy.backend.model.Action
import nl.dekkr.feedfrenzy.backend.util.ArticleExtractor
import org.scalatest.FlatSpecLike


class ArticleExtractorTest extends FlatSpecLike {

  val AE = new ArticleExtractor()

  val dateAuthorAction = Action(order = 1, actionType = "css-selector", inputVariable = None, outputVariable = Option("dateauthor"), template = Some("div.dateauthor"), replaceWith = None)
  val titleAction = Action(order = 2, actionType = "css-selector", inputVariable = None, outputVariable = Option("title"), template = Some("h2.node-title"), replaceWith = None)
  val authorAction = Action(order = 1, actionType = "css-selector", inputVariable = Option("dateauthor"), outputVariable = Option("author"), template = Some("a"), replaceWith = None)


  "ArticleExtractor" should "succeed on a single split action" in {
    val dummyUrl = "eff-tpp-fight.html"
    val content = getFileAsString(dummyUrl)

    val article = AE.getArticle(dummyUrl, content, List(dateAuthorAction, titleAction, authorAction))
    assert(article.author.contains("Maira Sutton"))
    assert(article.title == "Trade Officials Announce Conclusion of TPP—Now the Real Fight Begins")
  }


  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
