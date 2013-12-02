package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._

import models._

object Member extends Controller {
  
  def list = Action {
    Ok(html.member.list())
  }
  
  def create = Action {
    Ok(html.member.createForm())
  }
  
}