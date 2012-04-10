package controllers

import play.api._
import play.api.mvc._
import models.Book
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._

object Books extends Controller {
  
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
      "idLibrary" -> longNumber,
      "title" -> nonEmptyText,
      "isbn" -> nonEmptyText
    )(Book.apply)(Book.unapply)
  )
  
  def index = Action { Home }
  
  
  /**
   * Display the paginated list of books.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   */
  def list(page: Int, orderBy: Int) = Action { implicit request =>
    Ok(html.books.list(
      Book.list(page = page, orderBy = orderBy),
      orderBy
    ))
  }
  
  /**
   * Display the 'edit form' of a existing Book.
   *
   * @param id Id of the computer to edit
   */
  def edit(id: Long) = Action {
    Book.findById(id).map { book =>
      Ok(html.books.editForm(id, bookForm.fill(book)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the book to edit
   */
  def update(id: Long) = Action { implicit request =>
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
  def create = Action {
    Ok(html.books.createForm(bookForm))
  }
  
  /**
   * Handle the 'new book form' submission.
   */
  def save = Action { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.books.createForm(formWithErrors)),
      book => {
        Book.insert(book)
        Home.flashing("success" -> "Book %s has been created".format(book.isbn))
      }
    )
  }
  
  /**
   * Handle book deletion.
   */
  def delete(id: Long) = Action {
    Book.delete(id)
    Home.flashing("success" -> "Book has been deleted")
  }
  
}