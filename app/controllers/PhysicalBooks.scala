package controllers

import play.api._
import play.api.mvc._
import models.PhysicalBook
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._

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
    )(PhysicalBook.apply)(PhysicalBook.unapply)
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
  
}