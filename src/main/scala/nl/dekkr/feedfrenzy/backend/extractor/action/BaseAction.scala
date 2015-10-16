package nl.dekkr.feedfrenzy.backend.extractor.action

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.extractor.ValMap
import org.slf4j.LoggerFactory

trait BaseAction extends ValMap {

  protected val logger = Logger(LoggerFactory.getLogger(s"[${getClass.getName}]"))


  //def execute(vars: VariableMap, a: ActionType): List[String]

}
