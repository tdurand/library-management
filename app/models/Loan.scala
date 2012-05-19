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

  /**
   * Parse a (Loan,Option[PhysicalBook,Option[Book]]) from a ResultSet
   */
  val withBook = Loan.simple ~ (PhysicalBook.withBook ?) map {
    case loan~physicalbookWithBook => (loan,physicalbookWithBook)
  }
  
  /**
   * Parse a (Loan,Option[(PhysicalBook,Option[Book])],Option[User])) from a ResultSet
   */
  val withBookandUser = Loan.simple ~ (PhysicalBook.withBook ?) ~ (User.simple ?) map {
    case loan~physicalbookWithBook~user => (loan,physicalbookWithBook,user)
  }
  
  // -- Queries


  def findByIdWithUser(id:Long):Option[(Loan, Option[User])] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan left join user on loan.idUser = user.id where loan.id = {id}").on('id -> id).as(Loan.withUser.singleOpt)
    }
  }
  
  def findActiveLoanByPhysicalBookWithUser(idPhysicalBook:Long):Option[(Loan, Option[User])] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan left join user on loan.idUser = user.id where loan.idPhysicalBook = {idPhysicalBook} and loan.dateReturned is NULL").on('idPhysicalBook -> idPhysicalBook).as(Loan.withUser.singleOpt)
    }
  }

  def findByPhysicalBook(idPhysicalBook:Long):Option[Loan] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan where loan.idPhysicalBook = {idPhysicalBook}").on('idPhysicalBook -> idPhysicalBook).as(Loan.simple.singleOpt)
    }
  }

  def findActiveLoanByPhysicalBook(idPhysicalBook:Long):Option[Loan] = {
    DB.withConnection { implicit connection =>
      SQL("select * from loan where loan.idPhysicalBook = {idPhysicalBook} and loan.dateReturned is NULL").on('idPhysicalBook -> idPhysicalBook).as(Loan.simple.singleOpt)
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

  /**
   * Return a page of (Loan,Option[PhysicalBook,Option[Book]],Option[User]).
   *
   * @param page Page to display
   * @param pageSize Number of physicalbook per page
   * @param orderBy Book property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(activeLoan:Boolean,page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Loan,Option[(PhysicalBook,Option[Book])],Option[User])] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
        
      var activeQuery:String=""
      if(activeLoan) {
        activeQuery="is NULL"
      }
      else {
        activeQuery="is NOT NULL"
      }
      
      val loans = SQL(
        """
          select * from loan 
          left join physicalbook on loan.idPhysicalBook = physicalbook.id
          left join book on physicalbook.idBook = book.id
          left join user on loan.idUser = user.id
          where book.title like {filter} and
                loan.dateReturned """+activeQuery+"""
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(Loan.withBookandUser *)

      val totalRows = SQL(
        """
          select count(*) from loan 
          where loan.dateBorrowed like {filter} and
                loan.dateReturned """+activeQuery+"""
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(loans, page, offest, totalRows)
      
    }
    
  }

  def findActiveLoanByPhysicalBookId(idPhysicalBook:Long):Option[(Loan,Option[(PhysicalBook,Option[Book])],Option[User])] = {
     DB.withConnection { implicit connection =>
        val loan = SQL(
        """
          select * from loan 
          left join physicalbook on loan.idPhysicalBook = physicalbook.id
          left join book on physicalbook.idBook = book.id
          left join user on loan.idUser = user.id
          where physicalbook.id={idPhysicalBook} and
                loan.dateReturned is NULL
        """
      ).on(
        'idPhysicalBook -> idPhysicalBook 
      ).as(Loan.withBookandUser.singleOpt)

      return loan
    }
  }



}