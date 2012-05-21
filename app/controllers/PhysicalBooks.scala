package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._

object PhysicalBooks extends Controller {
  
  /**
   * This result directly redirect to the physicalbooks home.
   */
  val Home = Redirect(routes.PhysicalBooks.list(0, 2))
  
  def index = Action { Home }
  
  /**
   * Describe the physicalbook form (used in both edit and create screens).
   */ 
  val physicalbookForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "idBook" -> longNumber
    )
    ((id, idBook) => PhysicalBook(id,idBook))
    ((physicalbook: PhysicalBook) => Some(physicalbook.id, physicalbook.idBook))
  )
  
  /**
   * Display the paginated list of physicalbook.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.physicalbooks.list(
      PhysicalBook.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  /**
   * Handle physicalbook deletion.
   */
  def delete(id: Long) = Action {
    PhysicalBook.delete(id)
    Home.flashing("success" -> "PhysicalBook has been deleted")
  }

  /**
   * Display the 'add copy form'.
   */
  def create(idBook:Long) = Action {
    Book.findById(idBook).map { book =>
      Ok(html.physicalbooks.createForm(physicalbookForm.fill(PhysicalBook(NotAssigned,idBook)),book))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'add copy form' submission.
   */
  def save = Action { implicit request =>
    
    physicalbookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.physicalbooks.createForm(formWithErrors,Book.findById(physicalbookForm.data.get("idBook").get.toLong).get)),
      physicalbooks => {
        PhysicalBook.insert(physicalbooks)
        Home.flashing("success" -> "Copy %s has been created".format(physicalbooks.idBook))
      }
    )
  }

  def findPhysicalBookWithBook(idPhysicalBook:Long) = Action { implicit request =>
    PhysicalBook.findByIdWithBook(idPhysicalBook).map { physicalbookWithBook =>
      Ok(html.physicalbooks.showPhysicalBook(physicalbookWithBook))
    }.getOrElse(NotFound)
  }

  def tag(id: Long) = Action {
    Ok(html.books.tagBookRFID(id))
  }
  
}