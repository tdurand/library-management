package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Book(id : Pk[Long]= NotAssigned,title: String,isbn : String)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Book {
  
  // -- Parsers
  
  /**
   * Parse a Book from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("book.id") ~
    get[String]("book.title") ~
    get[String]("book.isbn")  map {
      case id~title~isbn => Book(id,title,isbn)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a book from the id.
   */
  def findById(id: Long): Option[Book] = {
    DB.withConnection { implicit connection =>
      SQL("select * from book where id = {id}").on('id -> id).as(Book.simple.singleOpt)
    }
  }
  
  /**
   * Update a book.
   */
  def update(id: Long, book: Book) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update book
          set title = {title}, idLibrary = 1, isbn = {isbn}
          where id = {id}
        """
      ).on(
        'id -> id,
        'title -> book.title,
        'isbn -> book.isbn
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new book.
   */
  def insert(book: Book) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into book values (nextval('book_seq'), 
            1,{title},{isbn}
          )
        """
      ).on(
        'title -> book.title,
        'isbn -> book.isbn
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a book.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from book where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
  /**
   * Return a page of Book.
   *
   * @param page Page to display
   * @param pageSize Number of books per page
   * @param orderBy Book property used for sorting
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1): Page[Book] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val books = SQL(
        """
          select * from book 
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'orderBy -> orderBy
      ).as(Book.simple *)

      val totalRows = SQL(
        """
          select count(*) from book 
        """
      ).as(scalar[Long].single)

      Page(books, page, offest, totalRows)
      
    }
    
  }
}