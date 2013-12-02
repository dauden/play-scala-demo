package controllers

import play.api._
import play.api.mvc._

import views._

object Application extends Controller {
  
  def index = Action {
    Ok(html.index())
  }
  
  def logout = Action {
    Ok(html.index())
  }
  
  def admin = Action {
    Ok(html.admin.list())
  }
  
  def inputs = Action {
    Ok(html.input.list())
  }
}