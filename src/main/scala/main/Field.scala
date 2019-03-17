package main

import scala.util.Random

class AttackerPossibleGoal(val point: (Int, Int), val defender: Squad)

class Field {

  private val fieldWidth = 15
  private val fieldHeight = 11
  private val field = Array.ofDim[ObjectOnField](fieldHeight, fieldWidth)
  initField()

  override def toString: String = {
    val fieldBuilder = StringBuilder.newBuilder
    for (line <- field) {
      fieldBuilder.append("|")
      for (objectOnField <- line) {
        fieldBuilder.append(objectOnField.nameOnField).append("|")
      }
      fieldBuilder.append("\n")
    }
    fieldBuilder.toString
  }

  def placeSquadOnField(squad: Squad, squadIndexInArmy: Int, squadsInArmy: Int, isAttackerSquad: Boolean) = {
    val j = if (isAttackerSquad) 0 else fieldWidth - 1
    val i = (fieldHeight.toDouble / (squadsInArmy + 1) * (squadIndexInArmy + 1)).toInt
    field(i)(j) = new SquadOnField(squad)
  }

  def move(attacker: Squad, newPoint: (Int, Int)) = {
    val attackerCoordinates = findSquadOnField(attacker)
    if (attackerCoordinates != newPoint) {
      val attackerOnField = field(attackerCoordinates._1)(attackerCoordinates._2)
      field(attackerCoordinates._1)(attackerCoordinates._2) = EmptyField
      field(newPoint._1)(newPoint._2) = attackerOnField
    }
  }

  /**
    * Точка на большом расстоянии.
    */
  private val undefinedPoint = (1000 * 1000, -1, -1)

  def findClosestEnemySquad(attacker: Squad): (Int, Int) = {
    val attackerCoordinates = findSquadOnField(attacker)
    val passedFields = Array.ofDim[Int](fieldHeight, fieldWidth)
    for (i <- 0 until fieldHeight) {
      for (j <- 0 until fieldWidth) {
        passedFields(i)(j) = undefinedPoint._1
      }
    }
    val fieldBetweenCurrentAndEnemy = findClosestEnemySquadFromPoint(attacker, attackerCoordinates, 0, passedFields, List())
    return if (fieldBetweenCurrentAndEnemy._1 == undefinedPoint._1) attackerCoordinates else (fieldBetweenCurrentAndEnemy._2, fieldBetweenCurrentAndEnemy._3)
  }


  /**
    * Поиск всех вражеских существ в радиусе достижимости атакующего отряда, расстояние - Манхетанское.
    *
    * Вражеский отряд может быть на расстоянии (скорость + 1), то есть атакующий подошел на максимальный радиус и ударил этот отряд.
    * Атаковать отряд может по вертикали или горизонтали относительно себя
    *
    * @param attacker
    * @return
    */
  def findAllEnemySquadsInRadius(attacker: Squad): List[AttackerPossibleGoal] = {
    val passedFields = Array.ofDim[Int](fieldHeight, fieldWidth)
    for (i <- 0 until fieldHeight) {
      for (j <- 0 until fieldWidth) {
        passedFields(i)(j) = undefinedPoint._1
      }
    }
    findAllEnemySquadsFromPoint(attacker, findSquadOnField(attacker), null, 0, passedFields)
      .sortWith(_.defender.totalHealth() < _.defender.totalHealth())
  }

  /**
    * Поиск ближайших вражеских отрядов из этой точки
    *
    * @param attacker
    * @param point
    * @param passedSteps
    * @param passedFields Клетки, посещенные в этот обход с минимальным расстояниям до нее
    * @return Расстояние до ближайшего врага и точка, на которую может ступить атакующий в этом ходу,
    *         чтобы добраться до этого врага
    */
  private def findClosestEnemySquadFromPoint(
                                              attacker: Squad, point: (Int, Int), passedSteps: Int, passedFields: Array[Array[Int]], path: List[(Int, Int)]
                                            ): (Int, Int, Int) = {
    if (point._1 < 0 || point._1 >= fieldHeight) return undefinedPoint
    if (point._2 < 0 || point._2 >= fieldWidth) return undefinedPoint
    if (passedSteps >= passedFields(point._1)(point._2) || passedSteps > fieldHeight + fieldWidth) return undefinedPoint
    passedFields(point._1)(point._2) = passedSteps
    field(point._1)(point._2) match {
      case squadOnField: SquadOnField if (squadOnField.isOtherSquadOnField(attacker)) =>
        if (squadOnField.areSquadsFromTheSameArmy(attacker) || squadOnField.isNotAlive) return undefinedPoint
        else return (passedSteps, path(attacker.speed)._1, path(attacker.speed)._2)
      case MountainOnField => return undefinedPoint
      case _ =>
    }
    val newPath = path :+ point
    var collectedEnemies = List(findClosestEnemySquadFromPoint(attacker, (point._1 + 1, point._2), passedSteps + 1, passedFields, newPath))
    collectedEnemies = collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1 - 1, point._2), passedSteps + 1, passedFields, newPath)
    collectedEnemies = collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1, point._2 + 1), passedSteps + 1, passedFields, newPath)
    collectedEnemies = collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1, point._2 - 1), passedSteps + 1, passedFields, newPath)
    val fieldBetweenCurrentAndEnemy = collectedEnemies.sortWith(_._1 < _._1).head
    return if (fieldBetweenCurrentAndEnemy._1 == undefinedPoint._1) undefinedPoint else fieldBetweenCurrentAndEnemy
  }


  /**
    *
    * @param attacker
    * @param point
    * @param passedSteps
    * @param passedFields
    * @return
    */
  private def findAllEnemySquadsFromPoint(attacker: Squad, point: (Int, Int), previousPoint: (Int, Int), passedSteps: Int, passedFields: Array[Array[Int]]): List[AttackerPossibleGoal] = {
    if (point._1 < 0 || point._1 >= fieldHeight) return List()
    if (point._2 < 0 || point._2 >= fieldWidth) return List()
    if (passedSteps > attacker.speed + 1 || passedSteps >= passedFields(point._1)(point._2)) return List()
    passedFields(point._1)(point._2) = passedSteps
    field(point._1)(point._2) match {
      case squadOnField: SquadOnField if (squadOnField.isOtherSquadOnField(attacker)) =>
        return if (squadOnField.areSquadsFromTheSameArmy(attacker) || squadOnField.isNotAlive) List() else List(new AttackerPossibleGoal(previousPoint, squadOnField.squad))
      case MountainOnField => return List()
      case _ =>
    }
    var result = findAllEnemySquadsFromPoint(attacker, (point._1 + 1, point._2), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1 - 1, point._2), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 + 1), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 - 1), point, passedSteps + 1, passedFields)
    result
  }

  private def findSquadOnField(squ: Squad): (Int, Int) = {
    for (i <- 0 until fieldHeight) {
      for (j <- 0 until fieldWidth) {
        field(i)(j) match {
          case squadOnField: SquadOnField if (squadOnField.isThisSquadOnField(squ)) => return (i, j)
          case _ =>
        }
      }
    }
    throw new IllegalStateException(s"Cannot find attacker $squ on field")
  }

  private def initField() = {
    for (i <- 0 until fieldHeight) {
      for (j <- 0 until fieldWidth) {
        field(i)(j) = EmptyField
      }
    }
    val MAX_MOUNTAINS = 5
    val MOUNTAINS_X = (2, fieldWidth - 2)
    val randomize = new Random()
    val mountains = 1 + randomize.nextInt(MAX_MOUNTAINS)
    for (i <- 0 until mountains) {
      val x = MOUNTAINS_X._1 + randomize.nextInt(MOUNTAINS_X._2 - MOUNTAINS_X._1 + 1)
      val y = randomize.nextInt(fieldHeight)
      field(y)(x) = MountainOnField
    }
  }
}
