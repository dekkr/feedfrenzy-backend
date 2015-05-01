package nl.dekkr.feedfrenzy.backend.services

import nl.dekkr.feedfrenzy.backend.model.{BackendResult, PageUrl}

trait BackendSystem {


  def getArticles(request: PageUrl): BackendResult
  def getArticle(request: PageUrl): BackendResult

}
