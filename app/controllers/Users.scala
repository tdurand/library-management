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
  def list(page: Int, orderBy: Int) = Action { implicit request =>
    Ok(html.users.list(
      User.list(page = page, orderBy = orderBy),
      orderBy
    ))
  }
  
  /**
   * Display the 'edit form' of a existing User.
   *
   * @param id Id of the computer to edit
   */
  def edit(id: Long) = Action {
    User.findById(id).map { user =>
      Ok(html.users.editForm(id, userForm.fill(user)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the user to edit
   */
  def update(id: Long) = Action { implicit request =>
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
  def create = Action {
    Ok(html.users.createForm(userForm))
  }
  
  /**
   * Handle the 'new user form' submission.
   */
  def save = Action { implicit request =>
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
  def delete(id: Long) = Action {
    User.delete(id)
    Home.flashing("success" -> "User has been deleted")
  }
  
}