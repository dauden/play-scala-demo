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
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText)
      (Member.apply)(Member.unapply)
      )

  def index = Action {
    Redirect(routes.MemberController.login)
  }
   
  def signUp = Action {implicit request =>
    Ok(views.html.authenForm())
  }
  
  def save = Action { implicit request =>
    memberForm.bindFromRequest.fold(
      formWithErrors => BadRequest("Signup Error"),
      member => {
        Member.insert(member)
        InputController.Home.flashing("success" -> "Welcome new Member!")
      })
  }

  /**
   * Authentication
   */
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => Member.authenticate(email, password).isDefined
      }))
  
   /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
   // var member_id: Long = 0
   //for(member <- Member.findByEmail())) {member_id = member.id.getOrElse(0)}
 
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
          user => {
            // To Do: para email --> member_id
            /*Member.findByEmail(user._1) match {
            case Some(member) => member_id = member.id.getOrElse(0)
            case None => member_id = -1
            }*/
          Redirect(routes.InputController.listBalance(0, 2, "", user._1)).withSession(Security.username -> user._1)
      })
  }
  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.MemberController.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }
  
}