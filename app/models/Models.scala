package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Member(id: Pk[Long] = NotAssigned, name: String, email: String, password: String)
case class InputData(id: Pk[Long] = NotAssigned, inputdate: Date, amount: Long, memberId: Long) //change name because of the same object Input 

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object InputData {
  
  // -- Parsers
  
  val simple = {
    get[Pk[Long]]("input.id") ~
    get[Date]("input.inputdate") ~
    get[Long]("input.amount") ~
    get[Long]("input.member_id") map {
      case id~inputdate~amount~memberId => InputData(id, inputdate, amount, memberId)
    }
  }
  
  val withMember = InputData.simple ~ (Member.simple ?) map {
    case input~member => (input,member)
  }
  
  // -- Queries
  
  def findById(id: Long): Option[InputData] = {
    DB.withConnection { (implicit connection =>
      SQL("select * from input where id = {id}").on('id -> id).as(InputData.simple.singleOpt))
    }
  }
  
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%" ): Page[(InputData, Option[Member])] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
    
    val inputs = SQL(
      """
        select * from input 
        left join member on input.member_id = member.id
        where input.inputdate like {filter}
        order by {orderBy} nulls last
        limit {pageSize} offset {offset}
      """
    ).on(
      'pageSize -> pageSize, 
      'offset -> offest,
      'filter -> filter,
      'orderBy -> orderBy
    ).as(InputData.withMember *)

    val totalRows = SQL(
      """
        select count(*) from input 
        left join member on input.member_id = member.id
        where input.inputdate like {filter}
      """
    ).on(
      'filter -> filter
    ).as(scalar[Long].single)

    Page(inputs, page, offest, totalRows)
      
    }
    
  }
  def listBalance(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%", email: String="%" ): Page[(InputData, Option[Member])] = {
    
    val offest = pageSize * page
    //i.id, i.inputdate,i.amount
    DB.withConnection {implicit connection =>
    
    val inputs = SQL(
      """
        select * from input as i,member as m
        where m.email ={email} and  i.member_id = m.id and i.inputdate like {filter}
        order by {orderBy} nulls last
        limit {pageSize} offset {offset}
      """
    ).on(
      'pageSize -> pageSize, 
      'offset -> offest,
      'filter -> filter,
      'orderBy -> orderBy,
      'email -> email
    ).as(InputData.withMember *)

    val totalRows = SQL(
      """
        select count(i.id) from input as i, member as m
        where m.email = {email} and i.member_id = m.id and  i.inputdate like {filter}
      """
    ).on(
        'email -> email,
        'filter -> filter
    ).as(scalar[Long].single)

    Page(inputs, page, offest, totalRows)
      
    }
    
  }
  
  def update(id: Long, input: InputData) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update input
          set inputdate = {inputdate}, amount = {amount}, member_id = {member_id}
          where id = {id}
        """
      ).on(
        'id -> id,
        'inputdate -> input.inputdate,
        'amount -> input.amount,
        'member_id -> input.memberId
      ).executeUpdate()
    }
  }
  
  def insert(input: InputData) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into input values (
            (select next value for input_seq), 
            {inputdate}, {amount}, {member_id}
          )
        """
      ).on(
        'inputdate -> input.inputdate,
        'amount -> input.amount,
        'member_id -> input.memberId
      ).executeUpdate()
    }
  }
  
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from input where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
}
object Member {
  
  val simple = {
    get[Pk[Long]]("member.id") ~
    get[String]("member.name") ~
    get[String]("member.email") ~
    get[String]("member.password") map {
      case id~name~email~password => Member(id,name,email,password)
    }
  }
  
  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
    SQL("select * from member order by name").as(Member.simple *).map(m => m.id.toString -> m.name)
  }
  
  def insert(member: Member) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into member values (
            (select next value for member_seq), 
            {name}, {email}, {password}
          )
        """
      ).on(
        'name -> member.name,
        'email -> member.email,
        'password -> member.password
      ).executeUpdate()
    }
  }
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[Member] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from member where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(Member.simple.singleOpt)
    }
  }
  
  
  /**
   * Retrieve a Member from id.
   */
  def findById(id: Long): Option[Member] = {
    DB.withConnection { implicit connection =>
      SQL("select * from member where id = {id}").on(
        'id -> id
      ).as(Member.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve a Member from email.
   */
  def findByEmail(email:  String): Option[Member] = {
    DB.withConnection { implicit connection =>
      SQL("select * from member where email = {email}").on(
        'email -> email
      ).as(Member.simple.singleOpt)
    }
  }
}