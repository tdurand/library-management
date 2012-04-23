package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id : Pk[Long]= NotAssigned,login:String,password:String)

object User {
  
  // -- Parsers
  
  /**
   * Parse a user from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("user.id") ~
    get[String]("user.login") ~
    get[String]("user.password")  map {
      case id~login~password => User(id,login,password)
    }
  }
  
  // -- Queries
  
  /**
   * Authenticate a User.
   */
  def authenticate(login: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         login = {login} and password = {password}
        """
      ).on(
        'login -> login,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve a user from the id.
   */
  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from User where id = {id}").on('id -> id).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Update a user.
   */
  def update(id: Long, user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update user
          set login = {login}, idLibrary = 1, password = {password}
          where id = {id}
        """
      ).on(
        'id -> id,
        'login -> user.login,
        'password -> user.password
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new user.
   */
  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (nextval('user_seq'), 
            1,{login},{password}
          )
        """
      ).on(
        'login -> user.login,
        'password -> user.password
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a user.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from user where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
  /**
   * Return a page of user.
   *
   * @param page Page to display
   * @param pageSize Number of users per page
   * @param orderBy user property used for sorting
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1): Page[User] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val users = SQL(
        """
          select * from user 
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'orderBy -> orderBy
      ).as(User.simple *)

      val totalRows = SQL(
        """
          select count(*) from user 
        """
      ).as(scalar[Long].single)

      Page(users, page, offest, totalRows)
      
    }
    
  }
}