package app.bitrader.api.common

import app.bitrader._
import app.bitrader.api.poloniex.OrdersBook
import json.accessor

/**
  * Created by Alex Afanasev
  */
@accessor case class Order( tpe: String,  rate: OrderKey,  amount: Option[OrderValue])

@accessor case class OrderBookContainer(orders: OrdersBook, changes: Seq[OrderWampMsg])


// wamp

sealed trait WampMsg

trait OrderProcessing {
  def bidNew(o: OrderPair)

  def askNew(o: OrderPair)

  def bidModify(o: OrderPair)

  def askModify(o: OrderPair)

  def askRemove(o: OrderPair)

  def bidRemove(o: OrderPair)
}


@accessor
case class OrderWampMsg(
                          var tpe: String,
                          var data: Order
                       ) extends WampMsg {
  def process(f: OrderProcessing) = {
    tpe match {
      case "newTrade" =>
        data.tpe match {
          case "sell" => f.askNew(data.rate, data.amount.get)
          case "buy" => f.bidNew(data.rate, data.amount.get)
        }
      case "orderBookModify" =>
        data.tpe match {
          case "ask" => f.askModify(data.rate, data.amount.get)
          case "bid" => f.bidModify(data.rate, data.amount.get)
        }
      case "orderBookRemove" =>
        data.tpe match {
          case "ask" => f.askRemove(data.rate.emptyPair)
          case "bid" => f.bidRemove(data.rate.emptyPair)
        }
    }
  }
}

@accessor case class ChatWampMsg( tpe: String,
                        messageNumber: Long,
                        username: String,
                        message: String,
                        reputation: Int
                      ) extends WampMsg


case class UserProfile(name: Option[String] = None, authData: Option[AuthData] = None)

case class AuthData(apiKey: String, apiSecret: String)