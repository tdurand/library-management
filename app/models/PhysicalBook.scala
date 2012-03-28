package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class PhysicalBook(id : Pk[Long]= NotAssigned,idBook:Long,idLoan:Long)

