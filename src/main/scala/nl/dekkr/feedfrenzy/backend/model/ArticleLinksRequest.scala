package nl.dekkr.feedfrenzy.backend.model

case class ArticleLinksRequest(url: String, actions: List[Action], raw : Option[Boolean])


