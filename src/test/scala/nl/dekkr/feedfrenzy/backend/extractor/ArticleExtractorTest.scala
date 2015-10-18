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

  val updateDateAction = Action(order = 7, actionType = "date-parser", inputVariable = Option("dateString"), outputVariable = Option("updatedDate"), template = None, replaceWith = None, locale = Some("en"), pattern = Some("invalid-mask"), padTime = Some(false))


  val author = "Maira Sutton"
  val title = "Trade Officials Announce Conclusion of TPP—Now the Real Fight Begins"
  val articleTags = List(
    "Fair Use and Intellectual Property: Defending the Balance",
    "International",
    "Trade Agreements and Digital Rights",
    "Trans-Pacific Partnership Agreement")

  val dummyUrl = "eff-tpp-fight.html"
  val content = getFileAsString(dummyUrl)

  val actions = List(dateAuthorAction, titleAction, authorAction, tagsAction, dateStrAction, createdDateAction, updateDateAction)



  "ArticleExtractor" should "extract a complete article" in {
    val article = AE.getArticle(dummyUrl, content, actions)
    assert(article.author.contains(author))
    assert(article.title == title)
    assert(article.tags == articleTags)
    assert(article.createdDate.nonEmpty)
    assert(article.createdDate.get.format(DateTimeFormatter.ofPattern("dd-MM-yyy")) == "05-10-2015")
    assert(article.updatedDate.isEmpty)
  }


  it should "extract the raw article components" in {
    val rawList = AE.getRaw(dummyUrl, content, actions)
    assert(rawList.variables.length == 8)
    assert(rawList.variables.find(v => v.name == "author").map(_.values).contains(List(author)))
    assert(rawList.variables.find(v => v.name == "title").map(_.values).contains(List(title)))
    assert(rawList.variables.find(v => v.name == "tags").map(_.values).contains(articleTags))
    assert(rawList.variables.find(v => v.name == "uid").map(_.values).contains(List("eff-tpp-fight.html")))
    assert(rawList.variables.find(v => v.name == "createdDate").map(_.values).contains(List("2015-10-05T00:00:00Z")))
    // Differs from the non-raw extraction, since the conversion to a date is not done
    assert(rawList.variables.find(v => v.name == "updatedDate").map(_.values).contains(List("October 5, 2015")))
  }

  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
