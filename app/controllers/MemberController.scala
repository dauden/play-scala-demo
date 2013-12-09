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
      "password" -> nonEmptyText)(Member.apply)(Member.unapply))

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => Member.authenticate(email, password).isDefined
      }))

  def index = Action {
    Redirect(routes.MemberController.login)
  }

  /**
   * Login process
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => {
        InputController.Home.withSession(Security.username -> user._1)
      })
  }
  
  /**
   * Signup process
   */
  def signUp = Action { implicit request =>
    Ok(html.signup())
  }

  def save = Action { implicit request =>
    memberForm.bindFromRequest.fold(
      formWithErrors => BadRequest("Signup Error"),
      member => {
        Member.insert(member)
        InputController.Home.flashing("success" -> "Welcome new Member!").withSession(Security.username -> member.email)
      })
  }
  
  /**
   * Logout process
   */
  def logout = Action {
    Redirect(routes.MemberController.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

}

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.MemberController.login)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: Member => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    Member.findByEmail(username).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }
  
}