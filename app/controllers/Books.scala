package controllers

import play.api._
import play.api.mvc._

object Books extends Controller {
  
  def index = Action {
    Ok(views.html.books.index())
  }
  
}