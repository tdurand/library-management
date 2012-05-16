package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import models._

case class PhysicalBook(id : Pk[Long]= NotAssigned,idBook:Long)

object PhysicalBook {
  
  // -- Parsers
  
  /**
   * Parse a PhysicalBook from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("physicalbook.id") ~
    get[Long]("physicalbook.idBook") map {
      case id~idBook => PhysicalBook(id,idBook)
    }
  }

  /**
   * Parse a (PhysicalBook,Book) from a ResultSet
   */
  val withBook = PhysicalBook.simple ~ (Book.simple ?) map {
    case physicalbook~book => (physicalbook,book)
  }
  
  // -- Queries
  
  /**
   * Retrieve a physicalbook from the id.
   */
  def findById(id: Long): Option[PhysicalBook] = {
    DB.withConnection { implicit connection =>
      SQL("select * from physicalbook where id = {id}").on('id -> id).as(PhysicalBook.simple.singleOpt)
    }
  }

  def findByIdWithBook(id:Long):Option[(PhysicalBook, Option[Book])] = {
    DB.withConnection { implicit connection =>
      SQL("select * from physicalbook left join book on physicalbook.idBook = book.id where physicalbook.id = {id}").on('id -> id).as(PhysicalBook.withBook.singleOpt)
    }
  }
  
  /**
   * Return a page of (PhysicalBook,Book).
   *
   * @param page Page to display
   * @param pageSize Number of physicalbook per page
   * @param orderBy Book property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(PhysicalBook, Option[Book])] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val physicalbooks = SQL(
        """
          select * from physicalbook 
          left join book on physicalbook.idBook = book.id
          where book.title like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(PhysicalBook.withBook *)

      val totalRows = SQL(
        """
          select count(*) from physicalbook 
          left join book on physicalbook.idBook = book.id
          where book.title like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(physicalbooks, page, offest, totalRows)
      
    }
    
  }
  
  /**
   * Insert a new physicalbook.
   */
  def insert(physicalbook: PhysicalBook) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into physicalbook values (nextval('physicalbook_seq'),{idBook})
        """
      ).on(
        'idBook -> physicalbook.idBook
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a physicalbook.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from physicalbook where id = {id}").on('id -> id).executeUpdate()
    }
  }
}