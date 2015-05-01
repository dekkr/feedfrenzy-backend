package nl.dekkr.feedfrenzy.backend.model

sealed trait BackendResult

case class ExistingContent(content: String) extends BackendResult

case class NewContent(content: String) extends BackendResult

case class NoContent() extends BackendResult

case class UnknownHost(uri: String) extends BackendResult

case class Error(exception: String) extends BackendResult
