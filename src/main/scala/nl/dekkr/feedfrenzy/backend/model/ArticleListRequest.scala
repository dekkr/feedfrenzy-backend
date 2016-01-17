package nl.dekkr.feedfrenzy.backend.model

case class ArticleListRequest(url: String, blockActions: List[Action], articleActions: List[Action], raw : Option[Boolean])


