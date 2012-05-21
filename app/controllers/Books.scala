package controllers

import play.api._
import play.api.mvc._
import models.Book
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._

object Books extends Controller with Secured {
  
  /**
   * This result directly redirect to the books home.
   */
  val Home = Redirect(routes.Books.list(0, 2))
  
  /**
   * Describe the book form (used in both edit and create screens).
   */ 
  val bookForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "title" -> nonEmptyText,
      "isbn" -> nonEmptyText,
      "copies" -> optional(number)
    )
    ((id, title, isbn, _) => Book(id,title,isbn))
    ((book: Book) => Some(book.id, book.title, book.isbn,Option(0)))
  )
  
  def index = Action { Home }
  
  
  /**
   * Display the paginated list of books.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   */
  def list(page: Int, orderBy: Int, filter: String) = IsAuthenticated { implicit user => implicit request =>
    Ok(html.books.list(
      Book.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  /**
   * Display the 'edit form' of a existing Book.
   *
   * @param id Id of the computer to edit
   */
  def edit(id: Long) = IsAuthenticated { implicit user => implicit request =>
    Book.findById(id).map { book =>
      Ok(html.books.editForm(id, bookForm.fill(book)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the book to edit
   */
  def update(id: Long) = IsAuthenticated { implicit user => implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.books.editForm(id, formWithErrors)),
      book => {
        Book.update(id, book)
        Home.flashing("success" -> "Book %s has been updated".format(book.isbn))
      }
    )
  }
  
  /**
   * Display the 'new book form'.
   */
  def create = IsAuthenticated { implicit user => implicit request =>
    Ok(html.books.createForm(bookForm))
  }
  
  /**
   * Handle the 'new book form' submission.
   */
  def save = IsAuthenticated { implicit user => implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.books.createForm(formWithErrors)),
      book => {
        Book.insert(book)
        Home.flashing("success" -> "Book %s : %s has been created".format(book.isbn,book.title))
      }
    )
  }
  
  /**
   * Handle book deletion.
   */
  def delete(id: Long) = IsAuthenticated { implicit user => implicit request =>
    Book.delete(id)
    Home.flashing("success" -> "Book has been deleted")
  }
  
}