package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Table(var tabname: String, 
                  var tabschema: String 
                )
object Table {
  
  /**
   * Parse a Project from a ResultSet
   */
  val table = {
    get[String]("tabname") ~
    get[String]("tabschema") map {
      case tabname~tabschema => Table(tabname,tabschema)
    }
  }
  
  def findTopTen(): List[Table] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT TABNAME, TABSCHEMA from SYSCAT.TABLES FETCH FIRST 10 ROWS ONLY")
       .as(table *)
    }
  }
}