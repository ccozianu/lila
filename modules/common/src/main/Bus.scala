package lila.common

import scala.util.NotGiven

import akka.actor.{ ActorRef, Scheduler }
import scalalib.bus.{ Channel, Tellable, NotBuseable }

val Bus = scalalib.bus.Bus(initialCapacity = 4096)

object actorBus:

  case class ActorTellable(ref: ActorRef) extends Tellable:
    def !(msg: Matchable) = ref ! msg

  extension (bus: scalalib.bus.Bus)

    // LOGIC : It is up to the caller to make sure `T`'s channel is relevant to the `tellable`
    inline def subscribeActorRef[T <: scalalib.bus.Bus.Payload](ref: ActorRef)(using
        NotGiven[T <:< NotBuseable]
    ) =
      Bus.subTellable[T](ActorTellable(ref))

    // LOGIC : It is up to the caller to make sure `T`'s channel is relevant to the `tellable`
    inline def unsubscribeActorRef[T <: scalalib.bus.Bus.Payload](ref: ActorRef)(using
        NotGiven[T <:< NotBuseable]
    ) =
      Bus.unsubUnchecked[T](ActorTellable(ref))

    // it's good to have subscribe and unsubscribe not taking channels the same way
    // to avoid calling one instead of the other
    def subscribeActorRefDyn(ref: ActorRef, to: Iterable[Channel]) =
      to.foreach(Bus.subscribeDyn(ActorTellable(ref), _))

    def unsubscribeActorRefDyn(ref: ActorRef, from: Channel) =
      Bus.unsubscribeDyn(ActorTellable(ref), List(from))
