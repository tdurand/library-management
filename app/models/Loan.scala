package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Loan(id : Pk[Long]= NotAssigned,idUser:Long,idPhysicalBook:Long,dateBorrowed:Date,dateDue:Date,dateReturned:Option[Date])

object Loan {
  
  // -- Parsers
  
  /**
   * Parse a Loan from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("loan.id") ~
    get[Long]("loan.idUser") ~
    get[Long]("loan.idPhysicalBook") ~
    get[Date]("loan.dateBorrowed") ~
    get[Date]("loan.dateDue") ~
    get[Option[Date]]("loan.dateReturned") map {
      case id~idUser~idPhysicalBook~dateBorrowed~dateDue~dateReturned => Loan(id,idUser,idPhysicalBook,dateBorrowed,dateDue,dateReturned)
    }
  }
  
  // -- Queries
  
  

}