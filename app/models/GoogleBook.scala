package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import java.util.{Date}

case class GoogleBook(title : String,
                      authors: List[String],
                      publishedDate : String,
                      description: String,
                      thumbnail:String)

object GoogleBook {
    
    implicit object GoogleBookReads extends Format[GoogleBook] {
        def reads(json: JsValue) = {
            (json \ "items").as[List[JsValue]].head match { case item =>
                (item \ "volumeInfo").as[JsValue] match { case volume =>
                    GoogleBook(
                        (volume \ "title").as[String],
                        (volume \ "authors").as[List[String]],
                        (volume \ "publishedDate").as[String],
                        (volume \ "description").as[String],
                        (volume \\ "thumbnail").head.as[String]
                    )
                }
            }
        }
        def writes(p: GoogleBook): JsValue = JsString("")
    }
}