package nl.dekkr.feedfrenzy.backend.model

sealed trait BackendResult

case class NewContent(body: String) extends BackendResult

case class Error(exception: String) extends BackendResult
