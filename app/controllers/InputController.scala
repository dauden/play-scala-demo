package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._  

object InputController extends Controller { 

  val Home = Redirect(routes.InputController.listBalance(0, 2, "",""))
  
  val inputForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "inputdate" -> date("yyyy-MM-dd"),
      "amount" -> longNumber,      
      "member" -> longNumber
    )(InputData.apply)(InputData.unapply)
  )
  
  // -- Actions
  
  /*def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      models.Input.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }*/
  
  def listBalance(page: Int, orderBy: Int, filter: String, email: String) = Action { implicit request =>
    Ok(html.list(
      InputData.listBalance(page = page, orderBy = orderBy, filter = ("%"+filter+"%"), email = email),
      orderBy, filter,email ))
  }
  
  
  def edit(id: Long) = Action {
    InputData.findById(id).map{input =>
      Ok(html.editForm(id,inputForm.fill(input),Member.options))}.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action { implicit request =>
    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id,formWithErrors, Member.options)),
      input => {
        InputData.update(id, input)
        Home.flashing("success" -> "An input has been updated")
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
        InputData.insert(input)
        Home.flashing("success" -> "New input has been created")
      }
    )
  }
  
  def delete(id: Long) = Action {
    InputData.delete(id)
    Home.flashing("success" -> "Complete deletion")
  }

}