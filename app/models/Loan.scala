package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Loan(id : Pk[Long]= NotAssigned,idUser:Long,idPhysicalBook:Long,dateBorrowed:Long,dateDue:Long,dateReturned:Long)

object Loan {
  
  // -- Parsers
  
  /**
   * Parse a Loan from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("loan.id") ~
    get[Long]("loan.idUser") ~
    get[Long]("loan.idPhysicalBook") ~
    get[Long]("loan.dateBorrowed") ~
    get[Long]("loan.dateDue") ~
    get[Long]("loan.dateReturned") map {
      case id~idUser~idPhysicalBook~dateBorrowed~dateDue~dateReturned => Loan(id,idUser,idPhysicalBook,dateBorrowed,dateDue,dateReturned)
    }
  }
  
  // -- Queries
  
  

}