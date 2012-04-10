package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id : Pk[Long]= NotAssigned,idLibrary:Long,firstName:String)