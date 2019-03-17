package main

import scala.collection.mutable.Queue

class SquadQueue(private val firstArmy: Army, private val secondArmy: Army) {

  private var queue = new Queue[Squad]

  /**
    * Заполняет очередь существ на новый ход, заодно показывает начало следующего хода.
    *
    * Очередь заполняется только живыми отрядами.
    */
  private def updateQueue() = {
    println("------Следующий ход------")
    queue ++= firstArmy.squads.filter(_.isAlive)
    queue ++= secondArmy.squads.filter(_.isAlive)
    queue = queue.sortWith(_.speed > _.speed)
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
  def getNextSquad(): Squad = {
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
