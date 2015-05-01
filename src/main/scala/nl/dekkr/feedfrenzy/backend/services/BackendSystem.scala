package nl.dekkr.feedfrenzy.backend.services

import nl.dekkr.feedfrenzy.backend.model.{BackendResult, PageUrl}

trait BackendSystem {


  def getContent(request: PageUrl): BackendResult

}
