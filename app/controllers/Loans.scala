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
  def returnLoan(bookId:Option[Long]) = Action {
    var book:Option[(PhysicalBook,Option[Book])]=None
    var loan:Option[(Loan,Option[User])]=None
    if(bookId.isDefined) {
        book = PhysicalBook.findByIdWithBook(bookId.get)
        loan = Loan.findByPhysicalBookWithUser(bookId.get)
    }
    Ok(html.loans.returnLoan(book,loan))
  }

  def selectUser(ownerId:Option[Long],bookId:Option[Long],page: Int= 0, orderBy: Int=2, filter: String="") = Action { implicit request =>
    Ok(html.loans.selectUser(
      ContextLoan(ownerId,bookId),
      User.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def selectBook(ownerId:Option[Long]=None,bookId:Option[Long]=None,action:String,page: Int= 0, orderBy: Int=2, filter: String="") = Action { implicit request =>
    Ok(html.loans.selectBook(
      ContextLoan(ownerId,bookId),
      action,
      PhysicalBook.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def save = TODO

  def close = TODO
}