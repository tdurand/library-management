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
  def newLoan(ownerId:Option[Long],bookId:Option[Long]) = Action {
    var user:Option[User]=None
    var book:Option[(PhysicalBook,Option[Book])]=None
    if(ownerId.isDefined) {
        user = User.findById(ownerId.get)
    }
    if(bookId.isDefined) {
        book = PhysicalBook.findByIdWithBook(bookId.get)
    }

    Ok(html.loans.newLoan(user,book,ContextLoan(ownerId,bookId)))
  }

  /**
   * Display the return loan page
  */
  def returnLoan = Action {
    Ok(html.loans.returnLoan())
  }

  def selectUser(ownerId:Option[Long],bookId:Option[Long],page: Int= 0, orderBy: Int=2, filter: String="") = Action { implicit request =>
    Ok(html.loans.selectUser(
      ContextLoan(ownerId:Option[Long],bookId:Option[Long]),
      User.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def selectBook(ownerId:Option[Long],bookId:Option[Long],page: Int= 0, orderBy: Int=2, filter: String="") = Action { implicit request =>
    Ok(html.loans.selectBook(
      ContextLoan(ownerId:Option[Long],bookId:Option[Long]),
      PhysicalBook.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def save = TODO
}