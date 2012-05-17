package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._

import java.util.Date

object Loans extends Controller {
  
  val Home = Redirect(routes.Loans.list())

  /**
   * Display the new loan page.
   */
  def newLoan(ownerId:Option[Long],bookId:Option[Long]) = Action { implicit request =>
    var user:Option[User]=None
    var book:Option[(PhysicalBook,Option[Book])]=None
    if(ownerId.isDefined) {
        user = User.findById(ownerId.get)
    }
    if(bookId.isDefined) {
        book = PhysicalBook.findByIdWithBook(bookId.get)
    }

    val dateBorrowed:Date= new java.util.Date()

    //Date due 15 day after
    val theFuture:Long = System.currentTimeMillis() + (86400 * 15 * 1000) //TODO externalize
    val dateDue:Date=new java.util.Date(theFuture)

    Ok(html.loans.newLoan(user,book,ContextLoan(ownerId,bookId),dateBorrowed,dateDue))
  }

  /**
   * Display the return loan page
  */
  def returnLoan(bookId:Option[Long]) = Action {
    var book:Option[(PhysicalBook,Option[Book])]=None
    var loan:Option[(Loan,Option[User])]=None

    if(bookId.isDefined) {
        book = PhysicalBook.findByIdWithBook(bookId.get)
        loan = Loan.findActiveLoanByPhysicalBookWithUser(bookId.get)
        loan.map(_._1.dateReturned=Some(new java.util.Date()))
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
    var loaned:Boolean=true
    if(action=="newLoan") {
        loaned=false
    } 

    Ok(html.loans.selectBook(
      ContextLoan(ownerId,bookId),
      action,
      PhysicalBook.listLoaned(loaned,page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  val saveLoanForm = Form( tuple(
                        "bookId" -> longNumber,
                        "ownerId" -> longNumber
                        ))

  def save() = Action { implicit request =>
    
    val dateBorrowed:Date= new java.util.Date()

    //Date due 15 day after
    val theFuture:Long = System.currentTimeMillis() + (86400 * 15 * 1000) //TODO externalize
    val dateDue:Date=new java.util.Date(theFuture)

    saveLoanForm.bindFromRequest.fold(
      formWithErrors => Home.flashing("error" -> "Error in parameters"),
      value => {
        PhysicalBook.findById(value._1).map { physicalbook =>
            physicalbook.loaned=true
            PhysicalBook.update(value._1,physicalbook)
        }.getOrElse(Home.flashing("error" -> "Error while updating PhysicalBook"))
        Loan.insert(Loan(NotAssigned,value._2,value._1,dateBorrowed,dateDue,None))
        Home.flashing("success" -> "Loan has been created")
      }
    )
  }

  val returnLoanForm = Form( "bookId" -> longNumber )

  def close = Action { implicit request =>
    val dateReturned:Date= new java.util.Date()
    returnLoanForm.bindFromRequest.fold(
      formWithErrors => Home.flashing("error" -> "Problem while closing"),
      bookId => Loan.findActiveLoanByPhysicalBook(bookId).map { loan =>
          loan.dateReturned=Some(dateReturned)

          PhysicalBook.findById(bookId).map { physicalbook =>
            physicalbook.loaned=false
            PhysicalBook.update(bookId,physicalbook)
          }.getOrElse(Home.flashing("error" -> "Error while updating PhysicalBook"))

          Loan.close(loan)
          Home.flashing("success" -> "Loan has been closed")
      }.getOrElse(Home.flashing("error" -> "Loan not found"))
   )
  }

  /**
   * Display the paginated list of loans.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.loans.list(
      Loan.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
}
    