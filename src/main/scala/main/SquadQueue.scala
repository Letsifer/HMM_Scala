package main

import scala.collection.mutable.Queue

class SquadQueue(private val firstArmy: Army, private val secondArmy: Army) {

  private var queue = new Queue[SquadInArmy]

  /**
    * Заполняет очередь существ на новый ход, заодно показывает начало следующего хода.
    *
    * Очередь заполняется только живыми отрядами.
    */
  private def updateQueue() = {
    println("------Следующий ход------")
    queue ++= firstArmy.squadsInArmy.filter(_.isAlive)
    queue ++= secondArmy.squadsInArmy.filter(_.isAlive)
    queue = queue.sortWith(_.getSpeed > _.getSpeed)
    firstArmy.heroInArmy.updateSpellUsage
    secondArmy.heroInArmy.updateSpellUsage
  }

  /**
    * Получение следующего юнита для хода.
    *
    * Если в очереди еще есть живые отряды, то находит первый живой среди них.
    * Если очередь пусть или все отряды в ней мертвы, то обновляет очередь.
    * Если после этого очередь пуста (то есть в армиях нет живых отрядов), то такая ситуация некорректна.
    *
    * @return
    */
  def nextSquad: SquadInArmy = {
    while (queue.nonEmpty) {
      val attacker = queue.dequeue
      if (attacker.isAlive) {
        return attacker
      }
    }
    updateQueue()
    if (queue.isEmpty) throw new ArrayIndexOutOfBoundsException("No alive attackers in queue")
    queue.dequeue()
  }
}
