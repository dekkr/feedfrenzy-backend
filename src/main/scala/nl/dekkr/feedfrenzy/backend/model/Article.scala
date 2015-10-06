package nl.dekkr.feedfrenzy.backend.model

import java.time.OffsetDateTime


case class Article(uid: String,
                   title: String,
                   author: Option[String] = None,
                   content: String,
                   createdDate: Option[OffsetDateTime] = None,
                   updatedDate: Option[OffsetDateTime] = None,
                   tags: List[String] = List.empty[String])


