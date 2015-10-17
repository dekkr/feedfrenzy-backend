package nl.dekkr.feedfrenzy.backend.extractor

import java.time.format.DateTimeFormatter

import nl.dekkr.feedfrenzy.backend.model.Action
import org.scalatest.FlatSpecLike


class ArticleExtractorTest extends FlatSpecLike {

  val AE = new ArticleExtractor()

  val dateAuthorAction = Action(order = 1, actionType = "css-selector", inputVariable = None, outputVariable = Option("dateauthor"), template = Some("div.dateauthor"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val titleAction = Action(order = 2, actionType = "css-selector", inputVariable = None, outputVariable = Option("title"), template = Some("h2.node-title"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val authorAction = Action(order = 3, actionType = "css-selector", inputVariable = Option("dateauthor"), outputVariable = Option("author"), template = Some("a"), replaceWith = None, locale = None, pattern = None, padTime = None)

  val tagsAction = Action(order = 4, actionType = "css-selector", inputVariable = None, outputVariable = Option("tags"), template = Some("div.field-type-taxonomy-term-reference > div > div > a"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val dateStrAction = Action(order = 5, actionType = "regex", inputVariable = Option("dateauthor"), outputVariable = Option("dateString"), template = Some("(?<month>\\w+)\\s(?<day>\\d+),\\s(?<year>\\d+)"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val createdDateAction = Action(order = 6, actionType = "date-parser", inputVariable = Option("dateString"), outputVariable = Option("createdDate"), template = None, replaceWith = None, locale = Some("en"), pattern = Some("MMMM d, yyyy"), padTime = Some(false))


  "ArticleExtractor" should "succeed on a single split action" in {
    val dummyUrl = "eff-tpp-fight.html"
    val content = getFileAsString(dummyUrl)

    val article = AE.getArticle(dummyUrl, content, List(dateAuthorAction, titleAction, authorAction, tagsAction, dateStrAction, createdDateAction))
    assert(article.author.contains("Maira Sutton"))
    assert(article.title == "Trade Officials Announce Conclusion of TPP—Now the Real Fight Begins")
    assert(article.tags == List(
      "Fair Use and Intellectual Property: Defending the Balance",
      "International",
      "Trade Agreements and Digital Rights",
      "Trans-Pacific Partnership Agreement")
    )
    assert(article.createdDate.nonEmpty)
    assert(article.createdDate.get.format(DateTimeFormatter.ofPattern("dd-MM-yyy")) == "05-10-2015")
  }

  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
