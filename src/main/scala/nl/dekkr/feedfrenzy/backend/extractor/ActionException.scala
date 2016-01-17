package nl.dekkr.feedfrenzy.backend.extractor

import nl.dekkr.feedfrenzy.backend.model.Action

class ActionException(action: Action, exceptionCause: Exception) extends Exception {

  import sext._

  override def getMessage = s"Exception in action: ${exceptionCause.getMessage}\nAction details: ${action.valueTreeString}"

}
