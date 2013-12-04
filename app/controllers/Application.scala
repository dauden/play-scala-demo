package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._

/**
 * Manage a database of computers
 */
object Application extends Controller { 

  val Home = Redirect(routes.Application.list(0, 2, ""))
  
  val inputForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "inputdate" -> date("yyyy-MM-dd"),
      "amount" -> longNumber,      
      "member" -> longNumber
    )(Input.apply)(Input.unapply)
  )
  
  // -- Actions
 
  def index = Action { Home }
  

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Input.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  def edit(id: Long) = Action {
    Input.findById(id).map { input =>
      Ok(html.editForm(id, inputForm.fill(input), Member.options))
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action { implicit request =>
    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, Member.options)),
      input => {
        Input.update(id, input)
        Home.flashing("success" -> "Input %s has been updated".format(input.id))
      }
    )
  }
  
  def create = Action {
    Ok(html.createForm(inputForm, Member.options))
  }
  
  def save = Action { implicit request =>
    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, Member.options)),
      input => {
        Input.insert(input)
        Home.flashing("success" -> "Input %s has been created".format(input.id))
      }
    )
  }
  
  def delete(id: Long) = Action {
    Input.delete(id)
    Home.flashing("success" -> "Complete deletion")
  }

}