package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models.User

object BookMashup extends Controller with Secured {
  
    def index = IsAuthenticated { implicit user => implicit request =>
        Ok(html.bookmashup.index())
    }
  
}