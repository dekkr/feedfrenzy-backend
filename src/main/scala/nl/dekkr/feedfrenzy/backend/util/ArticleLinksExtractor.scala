package nl.dekkr.feedfrenzy.backend.util

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model.{Action, ArticleLinks}
import org.slf4j.LoggerFactory


class ArticleLinksExtractor extends ActionExecutor {


  override protected val logger = Logger(LoggerFactory.getLogger("[ArticleLinksExtractor]"))

  def getList(input: String, actions: List[Action]): ArticleLinks =
    ArticleLinks(getVariable(Some(inputVar), doActions(Map(inputVar -> List(input)), actions.sortBy(a => a.order))))


  //TODO add raw variant, returning the map of all vars


}
