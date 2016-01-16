package nl.dekkr.feedfrenzy.backend.extractor

import nl.dekkr.feedfrenzy.backend.model.{Action, Article, RawVariable}
import org.scalatest.FlatSpecLike


class ArticleListExtractorTest extends FlatSpecLike {

  val AL = new ArticleListExtractor()

  val inputFile = "newcomments.html"

  val cssSelector = Action(order = 1, actionType = "css-selector", inputVariable = None, outputVariable = None, template = Some("table.itemlist tbody td.default "), replaceWith = None, locale = None, pattern = None, padTime = None)
  val blockActions = List(cssSelector)
  val blockActions2 = List(cssSelector.copy(outputVariable = Some("body")))

  val anchorSelector = Action(order = 1, actionType = "css-selector", inputVariable = None, outputVariable = Option("anchor"), template = Some("span.storyon"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val titleSelector = Action(order = 2, actionType = "css-selector", inputVariable = None, outputVariable = Option("title"), template = Some("span.storyon a"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val urlSelector = Action(order = 3, actionType = "attribute", inputVariable = Option("anchor"), outputVariable = Option("uid"), template = Some("href"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val contentSelector = Action(order = 4, actionType = "css-selector", inputVariable = None, outputVariable = Option("content"), template = Some("span.c00"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val contentCleaner1 = Action(order = 5, actionType = "css-remove", inputVariable = Option("content"), outputVariable = Option("content"), template = Some("div.reply"), replaceWith = None, locale = None, pattern = None, padTime = None)
  val contentCleaner2 = Action(order = 6, actionType = "replace", inputVariable = Option("content"), outputVariable = Option("content"), template = Some("<span> </span>"), replaceWith = Some(""), locale = None, pattern = None, padTime = None)
  val articleActions = List(anchorSelector, titleSelector, urlSelector, contentSelector, contentCleaner1, contentCleaner2)

  val exampleArticle = Article(
    uid = "https://news.ycombinator.com/item?id=10896217",
    title = "Show HN: A new kind of standing desk for $25 USD",
    author = None,
    content = "Finally an affordable standing desk.\n \n",
    createdDate = None,
    updatedDate = None,
    tags = List.empty)

  val exampleBlock = "<div style=\"margin-top:2px; margin-bottom:-10px;\"> \n <span class=\"comhead\"> <a href=\"https://news.ycombinator.com/user?id=drgath\">drgath</a> <span class=\"age\"><a href=\"https://news.ycombinator.com/item?id=10896678\">5 minutes ago</a></span> <span class=\"par\"> | <a href=\"https://news.ycombinator.com/item?id=10895962\">parent</a></span><span class=\"deadmark\"></span> <span class=\"storyon\"> | on: <a href=\"https://news.ycombinator.com/item?id=10893003\">Temperature and Temperament: Evidence from a Billi...</a></span> </span> \n</div><br><span class=\"comment\"> <span class=\"c00\">Many scientific studies confirm common sense. It's the process of turning assumptions into fact.<span> </span> \n  <div class=\"reply\"></div></span></span>"


  "ArticleListExtractor" should "return a list of articles" in {
    val content = getFileAsString(inputFile)

    val articles = AL.getList("dummyUrl", content, blockActions, articleActions)
    assert(articles.length == 30)
    assert(articles.contains(exampleArticle))
  }


  it should "return a raw list of block variables" in {
    val content = getFileAsString(inputFile)
    val vars = AL.getRawList("dummyUrl", content, blockActions2, articleActions)
    val bodyVar = vars.variables.filter(_.name == "body").head
    assert(bodyVar.values.length == 30)
    assert(bodyVar.values.contains(exampleBlock))
  }

  it should "return list of raw article variables" in {
    val content = getFileAsString(inputFile)
    val vars = AL.getRawArticles("dummyUrl", content, blockActions, articleActions)
    assert(vars.length == 30, "it should contain raw variables for 30 articles")
    val firstArticle = vars.head
    assert(firstArticle.variables.length == 4)
    assert(firstArticle.variables.contains(RawVariable("anchor", List("| on: <a href=\"https://news.ycombinator.com/item?id=10895961\">Microsoft open-sources ChakraCore JavaScript engin...</a>"))))
    assert(firstArticle.variables.contains(RawVariable("uid", List("https://news.ycombinator.com/item?id=10895961"))))
    assert(firstArticle.variables.contains(RawVariable("title", List("Microsoft open-sources ChakraCore JavaScript engin..."))))
    assert(firstArticle.variables.contains(RawVariable("content", List("&gt; An implementation of ChakraCore interpreter and runtime, no JIT, on Linux. Targeting x64 Ubuntu 15.10\n<p>I suspect the implementation is tightly coupled to Window's ABI.</p> \n"))))
  }

  def getFileAsString(fileName: String): String = {
    val file = io.Source.fromFile(s"./src/test/testware/pages/$fileName")
    try file.mkString finally file.close()
  }

}
