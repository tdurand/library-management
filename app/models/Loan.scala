package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Loan(id : Pk[Long]= NotAssigned,idUser:Long,idPhysicalBook:Long,dateBorrowed:Date,dateDue:Date,var dateReturned:Option[Date])

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

  /**
   * Parse a (Loan,Option[User]) from a ResultSet
   */
  val withUser = Loan.simple ~ (User.simple ?) map {
    case loan~user => (loan,user)
  }
  
  // -- Queries


  def findByIdWithUser(id:Long):Option[(Loan, Option[User])] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan left join user on loan.idUser = user.id where loan.id = {id}").on('id -> id).as(Loan.withUser.singleOpt)
    }
  }
  
  def findByPhysicalBookWithUser(idPhysicalBook:Long):Option[(Loan, Option[User])] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan left join user on loan.idUser = user.id where loan.idPhysicalBook = {idPhysicalBook}").on('idPhysicalBook -> idPhysicalBook).as(Loan.withUser.singleOpt)
    }
  }

  def findByPhysicalBook(idPhysicalBook:Long):Option[Loan] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan where loan.idPhysicalBook = {idPhysicalBook}").on('idPhysicalBook -> idPhysicalBook).as(Loan.simple.singleOpt)
    }
  }

  /**
   * Insert a new loan.
   */
  def insert(loan: Loan) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into loan values (nextval('loan_seq'), 
            {ownerId},{bookId},{dateBorrowed},{dateDue},null)
        """
      ).on(
        'ownerId -> loan.idUser,
        'bookId -> loan.idPhysicalBook,
        'dateBorrowed -> loan.dateBorrowed,
        'dateDue -> loan.dateDue
      ).executeUpdate()
    }
  }

  /**
   * Close a loan.
   */
  def close(loan: Loan) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update loan
          set dateReturned={dateReturned} 
          where id = {id}
        """
      ).on(
        'id -> loan.id,
        'dateReturned -> loan.dateReturned
      ).executeUpdate()
    }
  }



}