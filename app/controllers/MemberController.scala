package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._


object MemberController extends Controller { 

  val memberForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,      
      "password" -> nonEmptyText
    )(Member.apply)(Member.unapply)
  )
  
  def index = Action {
    Ok(html.authenForm())
  }
  
  def save = Action { implicit request =>
    memberForm.bindFromRequest.fold(
      formWithErrors => BadRequest("Signup Error"),
      member => {
        Member.insert(member)
        InputController.Home.flashing("success" -> "Welcome new Member!")
      }
    )
  }
  
}