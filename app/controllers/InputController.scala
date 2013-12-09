package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._

object InputController extends Controller with Secured {

  val inputForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "inputdate" -> date("yyyy-MM-dd"),
      "amount" -> longNumber,
      "member" -> longNumber)(InputData.apply)(InputData.unapply))
      
  def Home = Redirect(routes.InputController.listBalance(0, 2, ""))
  
  def listBalance(page: Int, orderBy: Int, filter: String) = withUser { member => implicit request =>
    Ok(html.list(
      InputData.listBalance(page = page, orderBy = orderBy, filter = ("%" + filter + "%"), email = member.email),
      orderBy, filter))
  }

  /*
   * Edit process
   */
  def edit(id: Long) = withUser { member => implicit request =>
    InputData.findByIdAndMember(id, member.id.get).map { input =>
      Ok(html.editForm(id, inputForm.fill(input), member))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = withUser { member => implicit request =>
    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, member)),
      input => {
        InputData.update(id, input)
        Home.flashing("success" -> "An input has been updated")
      })
  }

  /*
   * Create process
   */
  def create = withUser { member => implicit request =>
    Ok(html.createForm(inputForm, member))
  }

  def save = withUser { member => implicit request =>
    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, member)),
      input => {
        InputData.insert(input)
        Home.flashing("success" -> "New input has been created")
      })
  }

  /*
   * Delete
   */
  def delete(id: Long) = withUser { member => implicit request =>
    InputData.delete(id, member.id.get)
    Home.flashing("success" -> "Complete deletion")
  }

}