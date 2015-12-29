package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._

object Application extends Controller {

  def index = Action {
	  val tables = 
  		for (table <- Table.findTopTen) 
			yield Json.obj(
				"name" -> table.tabname,
				"schema" -> table.tabschema)
	   Ok(Json.arr(tables))
  }

}
