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
                       ) extends WampMsg{

  trait OrderProcessing{
    def bidNew(o: OrderPair)
    def askNew(o: OrderPair)
    def bidModify(o: OrderPair)
    def askModify(o: OrderPair)
    def askRemove(o: BigDecimal)
    def bidRemove(o: BigDecimal)
  }

  def process(f: OrderProcessing) = {
    tpe match {
      case "newTrade" =>
        data.tpe match {
          case "sell" => f.askNew((data.rate, data.amount.get))
          case "buy" => f.bidNew((data.rate, data.amount.get))
        }
      case "orderBookModify" =>
        data.tpe match {
          case "ask" => f.askModify((data.rate, data.amount.get))
          case "bid" => f.bidModify((data.rate, data.amount.get))
        }
      case "orderBookRemove" =>
        data.tpe match {
          case "ask" => f.askRemove(data.rate)
          case "bid" => f.bidRemove(data.rate)
        }
    }
  }
}

case class ChatWampMsg(@JsonProperty("type") tpe: String,
                       @JsonProperty("messageNumber") messageNumber: Long,
                       @JsonProperty("username") username: String,
                       @JsonProperty("message") message: String,
                       @JsonProperty("reputation") reputation: Int
                      ) extends WampMsg