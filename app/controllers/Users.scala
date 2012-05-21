package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models.User

object Users extends Controller with Secured {
  
  /**
   * This result directly redirect to the users home.
   */
  val Home = Redirect(routes.Users.list(0, 2))
  
  /**
   * Describe the user form (used in both edit and create screens).
   */ 
  val userForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )
  
  def index = Action { Home }
  
  
  /**
   * Display the paginated list of users.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   */
  def list(page: Int, orderBy: Int) = IsAuthenticated { implicit user => implicit request =>
    Ok(html.users.list(
      User.list(page = page, orderBy = orderBy),
      orderBy
    ))
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the user to edit
   */
  def update(id: Long) = IsAuthenticated { implicit user => implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.editForm(id, formWithErrors)),
      user => {
        User.update(id, user)
        Home.flashing("success" -> "User %s has been updated".format(user.login))
      }
    )
  }
  
  /**
   * Display the 'new user form'.
   */
  def create = IsAuthenticated { implicit user => implicit request =>
    Ok(html.users.createForm(userForm))
  }
  
  /**
   * Handle the 'new user form' submission.
   */
  def save = IsAuthenticated { implicit user => implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.createForm(formWithErrors)),
      user => {
        User.insert(user)
        Home.flashing("success" -> "User %s has been created".format(user.login))
      }
    )
  }
  
  /**
   * Handle user deletion.
   */
  def delete(id: Long) = IsAuthenticated { implicit user => implicit request =>
    User.delete(id)
    Home.flashing("success" -> "User has been deleted")
  }

  /**
   * Display the 'edit form' of a existing User.
   *
   * @param id Id of the user to edit
   */
  def edit(id: Long) = IsAuthenticated { implicit user => implicit request =>
    User.findById(id).map { theuser =>
      Ok(html.users.editForm(id, userForm.fill(theuser)))
    }.getOrElse(NotFound)
  }

  /**
   * Display the users details of a existing User.
   *
   * @param id Id of the user see details
   */
  def details(id: Long) = IsAuthenticated { implicit user => implicit request =>
    val details=User.details(id)
    Ok(html.users.details(details.head._1,details))
  }

  def findUserById(idUser:Long) = IsAuthenticated { implicit user => implicit request =>
    User.findById(idUser).map { user =>
      Ok(html.users.showUser(user))
    }.getOrElse(NotFound)
  }

  def tag(id: Long) = IsAuthenticated { implicit user => implicit request =>
    Ok(html.users.tagUserRFID(id))
  }
  
}