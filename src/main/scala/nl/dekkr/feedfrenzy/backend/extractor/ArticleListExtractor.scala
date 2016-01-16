package nl.dekkr.feedfrenzy.backend.extractor

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model.{Action, Article, RawVariables}
import org.slf4j.LoggerFactory


class ArticleListExtractor extends ActionExecutor {


  override protected val logger = Logger(LoggerFactory.getLogger("[ArticleListExtractor]"))

  val AL = new ArticleLinksExtractor
  val AR = new ArticleExtractor

  def getList(url: String, input: String, blockActions: List[Action], articleActions: List[Action]): Seq[Article] =
     AL.getList(input,blockActions).urls.map{ case e => AR.getArticle(url,e,articleActions)}

  def getRawList(url: String, input: String, blockActions: List[Action], articleActions: List[Action]): RawVariables =
    AL.getRaw(input,blockActions)

  def getRawArticles(url: String, input: String, blockActions: List[Action], articleActions: List[Action]): Seq[RawVariables] =
    AL.getList(input,blockActions).urls.map{ case e => RawVariables(variables= AR.getRaw(url,e,articleActions).variables,input=e)}

}
