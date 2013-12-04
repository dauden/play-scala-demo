package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Member(id: Pk[Long] = NotAssigned, name: String, email: String, password: String)
case class Input(id: Pk[Long] = NotAssigned, inputdate: Date, amount: Long, memberId: Long)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Input {
  
  // -- Parsers
  
  /**
   * Parse a Computer from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("input.id") ~
    get[Date]("input.inputdate") ~
    get[Long]("input.amount") ~
    get[Long]("input.member_id") map {
      case id~inputdate~amount~memberId => Input(id, inputdate, amount, memberId)
    }
  }
  
  /**
   * Parse a (Computer,Company) from a ResultSet
   */
  val withMember = Input.simple ~ (Member.simple ?) map {
    case input~member => (input,member)
  }
  
  // -- Queries
  
  /**
   * Retrieve a computer from the id.
   */
  def findById(id: Long): Option[Input] = {
    DB.withConnection { implicit connection =>
      SQL("select * from input where id = {id}").on('id -> id).as(Input.simple.singleOpt)
    }
  }
  
  /**
   * Return a page of (Computer,Company).
   *
   * @param page Page to display
   * @param pageSize Number of computers per page
   * @param orderBy Computer property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Input, Option[Member])] = {
    
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
      ).as(Input.withMember *)

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
  
  /**
   * Update a computer.
   *
   * @param id The computer id
   * @param computer The computer values.
   */
  def update(id: Long, input: Input) = {
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
  
  /**
   * Insert a new computer.
   *
   * @param computer The computer values.
   */
  def insert(input: Input) = {
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
  
  /**
   * Delete a computer.
   *
   * @param id Id of the computer to delete.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from input where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
}

object Member {
    
  /**
   * Parse a Company from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("member.id") ~
    get[String]("member.name") ~
    get[String]("member.email") ~
    get[String]("member.password") map {
      case id~name~email~password => Member(id,name,email,password)
    }
  }
  
  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
    SQL("select * from member order by name").as(Member.simple *).map(m => m.id.toString -> m.name)
  }
  
}