package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Book(id : Pk[Long]= NotAssigned,idLibrary:Long,title: String,isbn : String)

object Book {
  
  // -- Parsers
  
  /**
   * Parse a Book from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("book.id") ~
    get[Long]("book.idLibrary") ~
    get[String]("book.title") ~
    get[String]("book.isbn")  map {
      case id~idLibrary~title~isbn => Book(id,idLibrary,title,isbn)
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
          set title = {title}, idLibrary = {idLibrary}, isbn = {isbn}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> book.title,
        'idLibrary -> book.idLibrary,
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
          insert into book values ('', 
            {name}, {idLibrary}, {isbn}
          )
        """
      ).on(
        'name -> book.title,
        'idLibrary -> book.idLibrary,
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
}