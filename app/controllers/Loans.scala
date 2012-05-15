package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._

object Loans extends Controller {
  
  val Home = Redirect(routes.Loans.index)

  def index = Action { 
    Ok(html.loans.index()) 
  }

  /**
   * Display the new loan page.
   */
  def newLoan = Action {
    Ok(html.loans.newLoan())
  }

  /**
   * Display the return loan page
  */
  def returnLoan = Action {
    Ok(html.loans.returnLoan())
  }

  def save = TODO
}