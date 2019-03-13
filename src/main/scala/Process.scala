import scala.collection.mutable.Queue


class Process(private val firstArmy: Army, private val secondArmy: Army) {

  private var queue = new Queue[Squad]

  private val fieldWidth = 10
  private val fieldHeight = 10
  private val field = Array.ofDim[ObjectOnField](fieldWidth, fieldHeight)
  placeSquadsOnField()

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

  def battle(): BattleResult.BattleResult = {
    while (firstArmy.isAlive() && secondArmy.isAlive()) {
      println("------Следующий ход------")
      queue ++= firstArmy.squads
      queue ++= secondArmy.squads
      queue = queue.sortWith(_.speed > _.speed)
      while (queue.nonEmpty) {
        val attacker = chooseNextAttacker()
        if (attacker != EmptySquad) {
          println(s"Ходят $attacker")
          val enemySquadsInRadius = findAllEnemySquadsInRadius(attacker)
          if (enemySquadsInRadius.isEmpty) {
            //отряд может не дойти ни до одного вражеского отряда
            move(attacker, findClosestEnemySquad(attacker))
          } else {
            //если findAllEnemySquadsInRadius вернет пустой список (учесть потом, атакующий отряд может как-то дойти до этой точки)
            // то поиск ближайшего вражеского отряда и смещение туда
            // иначе выбор цели атаки, смещение на соседнюю свободную клетку и атака
            val attackerArmy = attacker.army
            val defenderArmy = if (attackerArmy == firstArmy) secondArmy else firstArmy
            move(attacker, enemySquadsInRadius.head.point)
            val defender = enemySquadsInRadius.head.defender
            val attackResult = attacker.attack(defender, calculateAttackModifier(attacker, attackerArmy.hero, defender, defenderArmy.hero))
            if (attackResult.killedCreatures > 0) {
              println(s"${attacker.name} нанесли ${attackResult.resultDamage} урона, убито ${attackResult.killedCreatures} ${defender.name}")
            } else {
              println(s"${attacker.name} нанесли ${attackResult.resultDamage} урона ${defender.name}")
            }
            if (attackResult.wereAllCreaturesInDefenderSquadKilled) {
              if (defenderArmy.isNotAlive()) {
                if (attackerArmy == firstArmy) return BattleResult.FIRST_WIN
                else return BattleResult.SECOND_WIN
              }
            }
          }
        }
      }
    }
    throw new RuntimeException("Incorrect place in battle method")
  }

  private def move(attacker: Squad, newPoint: (Int, Int)) = {
    val attackerCoordinates = findAttackerOnField(attacker)
    if (!attackerCoordinates.equals(newPoint)) {
      //TODO println movement
      val attackerOnField = field(attackerCoordinates)
      field(attackerCoordinates) = null
      field(newPoint) = attackerOnField
    }
  }

  /**
    * Точка на большом расстоянии.
    */
  private val undefinedPoint = (1000 * 1000, -1, -1)

  private def findClosestEnemySquad(attacker: Squad): (Int, Int) = {
    val attackerCoordinates = findAttackerOnField(attacker)
    val passedFields = Array.ofDim[Int](fieldWidth, fieldHeight)
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
        passedFields(i)(j) = undefinedPoint._1
      }
    }
    val fieldBetweenCurrentAndEnemy = findClosestEnemySquadFromPoint(attacker, attackerCoordinates, 0, passedFields, List())
    return if (fieldBetweenCurrentAndEnemy._1 == undefinedPoint._1) attackerCoordinates else (fieldBetweenCurrentAndEnemy._1, fieldBetweenCurrentAndEnemy._2)
  }

  /**
    * Поиск ближайших вражеских отрядов из этой точки
    *
    * TODO возвращается координата врага, а не координата, с которой его можно атаковать
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
    if (passedSteps >= passedFields(point._1)(point._2)) return undefinedPoint
    passedFields(point._1)(point._2) = passedSteps
    if (field(point._1)(point._2) != null) {
      field(point._1)(point._2) match {
        case squadOnField: SquadOnField =>
          if (!squadOnField.isThisSquadOnField(attacker)) {
            if (squadOnField.areSquadsFromTheSameArmy(attacker)) return undefinedPoint
            else return (passedSteps, path(attacker.speed)._1, path(attacker.speed)._2)
          }
      }
    }
    val newPath = List(path) :+ point
    val collectedEnemies = List(findClosestEnemySquadFromPoint(attacker, (point._1 + 1, point._2), passedSteps + 1, passedFields, newPath))
    collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1 - 1, point._2), passedSteps + 1, passedFields, newPath)
    collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1, point._2 + 1), passedSteps + 1, passedFields, newPath)
    collectedEnemies :+ findClosestEnemySquadFromPoint(attacker, (point._1, point._2 - 1), passedSteps + 1, passedFields, newPath)
    val fieldBetweenCurrentAndEnemy = collectedEnemies.sortWith(_._1 < _._1).head
    return if (fieldBetweenCurrentAndEnemy._1 == undefinedPoint._1) undefinedPoint else fieldBetweenCurrentAndEnemy
  }

  class AttackerPossibleGoal(val point: (Int, Int), val defender: Squad)

  /**
    * Поиск всех вражеских существ в радиусе достижимости атакующего отряда, расстояние - Манхетанское.
    *
    * Вражеский отряд может быть на расстоянии (скорость + 1), то есть атакующий подошел на максимальный радиус и ударил этот отряд.
    * Атаковать отряд может по вертикали или горизонтали относительно себя
    *
    * TODO Possible Bug - через точку, которая первой попала в метод, расстояние может быть больше, чем через другую.
    * Например атакующий стоит в точке (5,5), в точках (6, 5) и (6, 4) стоят союзники, а в точке (5,7) - враг.
    * ДФС пойдет через точку (5,4) и найдет врага на расстоянии 6, а можно было пройти через точку (5,6), тогда расстояние будет 3.
    *
    * @param attacker
    * @return
    */
  private def findAllEnemySquadsInRadius(attacker: Squad): List[AttackerPossibleGoal] = {
    val passedFields = Array.ofDim[Boolean](fieldWidth, fieldHeight)
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
        passedFields(i)(j) = false
      }
    }
    findAllEnemySquadsFromPoint(attacker, findAttackerOnField(attacker), 0, passedFields)
  }

  /**
    * TODO Не совсем то возвращается - возвращается координата врага, а нужно координата, с которой атака идет, т.е. предыдущая точка
    *
    * @param attacker
    * @param point
    * @param passedSteps
    * @param passedFields
    * @return
    */
  private def findAllEnemySquadsFromPoint(attacker: Squad, point: (Int, Int), passedSteps: Int, passedFields: Array[Array[Boolean]]): List[AttackerPossibleGoal] = {
    if (point._1 < 0 || point._1 >= fieldWidth) return List()
    if (point._2 < 0 || point._2 >= fieldHeight) return List()
    if (passedSteps > attacker.speed) return List()
    if (passedFields(point._1)(point._2)) return List()
    passedFields(point._1)(point._2) = true
    if (field(point._1)(point._2) != null) {
      field(point._1)(point._2) match {
        case squadOnField: SquadOnField =>
          if (!squadOnField.isThisSquadOnField(attacker)) {
            if (squadOnField.areSquadsFromTheSameArmy(attacker)) return List() else return List(new AttackerPossibleGoal(point, squadOnField.squad))
          }
      }
    }
    val result = findAllEnemySquadsFromPoint(attacker, (point._1 + 1, point._2), passedSteps + 1, passedFields)
    result ::: findAllEnemySquadsFromPoint(attacker, (point._1 - 1, point._2), passedSteps + 1, passedFields)
    result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 + 1), passedSteps + 1, passedFields)
    result ::: findAllEnemySquadsFromPoint(attacker, (point._1, point._2 - 1), passedSteps + 1, passedFields)
    result
  }

  private def findAttackerOnField(attacker: Squad): (Int, Int) = {
    for (i <- 0 until fieldWidth) {
      for (j <- 0 until fieldHeight) {
        field(i)(j) match {
          case squadOnField: SquadOnField => if (squadOnField.isThisSquadOnField(attacker)) return (i, j)
        }
      }
    }
    throw new IllegalStateException(s"Cannot find attacker $attacker on field")
  }

  private val attackModifierCoeficient = 0.05

  private def calculateAttackModifier(attacker: Squad, attackerHero: Hero, defender: Squad, defenderHero: Hero): Double = {
    val attack = attacker.attack + attackerHero.attack
    val defence = defender.defence + defenderHero.defense
    if (attack >= defence) 1 + (attack - defence) * attackModifierCoeficient
    else 1 / (1 + (defence - attack) * attackModifierCoeficient)
  }

  private def chooseNextAttacker(): Squad = {
    while (queue.nonEmpty) {
      val attacker = queue.dequeue
      if (attacker.isAlive()) {
        return attacker
      }
    }
    EmptySquad
  }

  private def placeSquadsOnField() = {
    for (i <- firstArmy.squads.indices) {
      field(i)(0) = new SquadOnField(firstArmy.squads(i))
    }
    for (i <- secondArmy.squads.indices) {
      field(i)(fieldWidth - 1) = new SquadOnField(secondArmy.squads(i))
    }
  }

}
