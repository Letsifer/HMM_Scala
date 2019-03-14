class AttackerPossibleGoal(val point: (Int, Int), val defender: Squad)

class Field {

  private val fieldWidth = 10
  private val fieldHeight = 10
  private val field = Array.ofDim[ObjectOnField](fieldWidth, fieldHeight)

  def getTextualField(): String = {
    val fieldBuilder = StringBuilder.newBuilder
    for (line <- field) {
      fieldBuilder.append("|")
      for (objectOnField <- line) {
        val objectText = if (objectOnField != null) objectOnField.nameOnField() else " "
        fieldBuilder.append(objectText).append("|")
      }
      fieldBuilder.append("\n")
    }
    fieldBuilder.toString()
  }

  def placeSquadOnField(squad: Squad, squadIndexInArmy: Int, isAttackerSquad: Boolean) = {
    val x = if (isAttackerSquad) 0 else fieldWidth - 1
    val y = squadIndexInArmy
    field(y)(x) = new SquadOnField(squad)
  }

  def move(attacker: Squad, newPoint: (Int, Int)) = {
    val attackerCoordinates = findSquadOnField(attacker)
    if (!attackerCoordinates.equals(newPoint)) {
      //TODO println movement
      val attackerOnField = field(attackerCoordinates._1)(attackerCoordinates._2)
      field(attackerCoordinates._1)(attackerCoordinates._2) = null
      field(newPoint._1)(newPoint._2) = attackerOnField
    }
  }

  /**
    * Точка на большом расстоянии.
    */
  private val undefinedPoint = (1000 * 1000, -1, -1)

  def findClosestEnemySquad(attacker: Squad): (Int, Int) = {
    val attackerCoordinates = findSquadOnField(attacker)
    val passedFields = Array.ofDim[Int](fieldWidth, fieldHeight)
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
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
    val passedFields = Array.ofDim[Int](fieldWidth, fieldHeight)
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
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
    if (point._1 < 0 || point._1 >= fieldWidth) return undefinedPoint
    if (point._2 < 0 || point._2 >= fieldHeight) return undefinedPoint
    if (passedSteps >= passedFields(point._1)(point._2) || passedSteps > fieldHeight + fieldWidth) return undefinedPoint
    passedFields(point._1)(point._2) = passedSteps
    if (field(point._1)(point._2) != null) {
      field(point._1)(point._2) match {
        case squadOnField: SquadOnField =>
          if (!squadOnField.isThisSquadOnField(attacker)) {
            if (squadOnField.areSquadsFromTheSameArmy(attacker) || squadOnField.isNotAlive()) return undefinedPoint
            else return (passedSteps, path(attacker.speed)._1, path(attacker.speed)._2)
          }
      }
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
    if (point._1 < 0 || point._1 >= fieldWidth) return List()
    if (point._2 < 0 || point._2 >= fieldHeight) return List()
    if (passedSteps > attacker.speed + 1 || passedSteps >= passedFields(point._1)(point._2)) return List()
    passedFields(point._1)(point._2) = passedSteps
    if (field(point._1)(point._2) != null) {
      field(point._1)(point._2) match {
        case squadOnField: SquadOnField =>
          if (!squadOnField.isThisSquadOnField(attacker)) {
            if (squadOnField.areSquadsFromTheSameArmy(attacker) || squadOnField.isNotAlive()) return List() else return List(new AttackerPossibleGoal(previousPoint, squadOnField.squad))
          }
      }
    }
    var result = findAllEnemySquadsFromPoint(attacker, (point._1 + 1, point._2), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1 - 1, point._2), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 + 1), point, passedSteps + 1, passedFields)
    result = result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 - 1), point, passedSteps + 1, passedFields)
    result
  }

  private def findSquadOnField(squ: Squad): (Int, Int) = {
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
        if (field(i)(j) != null) {
          field(i)(j) match {
            case squadOnField: SquadOnField => if (squadOnField.isThisSquadOnField(squ)) return (i, j)
          }
        }
      }
    }
    throw new IllegalStateException(s"Cannot find attacker $squ on field")
  }

}