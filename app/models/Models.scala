package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Member(id: Pk[Long] = NotAssigned, name: String, email: String, password: String, acl: String, reportOn: Date)
case class Input(id: Pk[Long] = NotAssigned, name: String, createOn: Date, amount: Double, memberId: Long)

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
   * Parse a Input from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("input.id") ~
    get[String]("input.name") ~
    get[Date]("input.createOn") ~
    get[Double]("input.amount") ~
    get[Long]("input.memberId") map {
      case id~name~createOn~amount~memberId => Input(id, name, createOn, amount, memberId)
    }
  }
  
  /**
   * Parse a (Computer,Company) from a ResultSet
   */
  val withMember = Input.simple ~ (Member.simple ?) map {
    case input~member => (input,member)
  }
  
  // -- Queries
  
}

object Member {
  
  // -- Parsers
    
  /**
   * Parse a Member from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("member.id") ~
    get[String]("member.name") ~
    get[String]("member.email") ~
    get[String]("member.password") ~
    get[String]("member.acl") ~
    get[Date]("member.reportOn") map {
      case id~name~email~password~acl~reportOn => Member(id, name, email, password, acl, reportOn)
    }
  }
  
  // -- Queries
  
}

