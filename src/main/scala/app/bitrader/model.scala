package app.bitrader

import app.bitrader.api.poloniex.OrdersBook
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

/**
  * Created by Alex Afanasev
  */
@JsonCreator
case class Order(@JsonProperty("type") tpe: String, @JsonProperty("rate") rate: BigDecimal, @JsonProperty("amount") amount: Option[BigDecimal])

case class OrderBookContainer(orders: OrdersBook, changes: Seq[OrderWampMsg])


// wamp

sealed trait WampMsg

@JsonCreator
case class OrderWampMsg(
                         @JsonProperty("type") var tpe: String,
                         @JsonProperty("data") var data: Order
                       ) extends WampMsg

case class ChatWampMsg(@JsonProperty("type") tpe: String,
                       @JsonProperty("messageNumber") messageNumber: Long,
                       @JsonProperty("username") username: String,
                       @JsonProperty("message") message: String,
                       @JsonProperty("reputation") reputation: Int
                      ) extends WampMsg