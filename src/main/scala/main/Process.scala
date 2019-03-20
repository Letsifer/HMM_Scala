package main

import hero.Hero


class Process(private val firstArmy: Army, private val secondArmy: Army) {

  private val queue = new SquadQueue(firstArmy, secondArmy)
  private val field = new Field
  placeSquadsOnField()

  override def toString: String = (s"${field}\n${firstArmy.getFullInfo}\n${secondArmy.getFullInfo}")

  def battle(): BattleResult.BattleResult = {
    println("__________At start___________")
    while (firstArmy.isAlive && secondArmy.isAlive) {
      println(this)
      val attacker = queue.nextSquad
      val attackerArmy = attacker.army
      val defenderArmy = if (attackerArmy == firstArmy) secondArmy else firstArmy
      attackerArmy.heroInArmy.useSpellOnSquad(defenderArmy)
      if (defenderArmy.isNotAlive) {
        return if (attackerArmy == firstArmy) BattleResult.FIRST_WIN else BattleResult.SECOND_WIN
      }
      println(s"Ходят $attacker")
      val enemySquadsInRadius = field.findAllEnemySquadsInRadius(attacker)
      if (enemySquadsInRadius.isEmpty) {
        //отряд не может дойти ни до одного вражеского отряда за этот ход
        field.move(attacker, field.findClosestEnemySquad(attacker))
      } else {
        //если findAllEnemySquadsInRadius вернет пустой список
        // то поиск ближайшего вражеского отряда и смещение туда
        // иначе выбор цели атаки, смещение на соседнюю свободную клетку и атака
        field.move(attacker, enemySquadsInRadius.head.point)
        val defender = enemySquadsInRadius.head.defender
        val attackResult = attacker.attack(defender, calculateAttackModifier(attacker.squad, attackerArmy.heroInArmy.hero, defender.squad, defenderArmy.heroInArmy.hero))
        if (attackResult.areCreaturesKilled()) {
          println(s"${attacker} нанесли ${attackResult.resultDamage} урона, убито ${attackResult.killedCreatures} ${defender}")
        } else {
          println(s"${attacker} нанесли ${attackResult.resultDamage} урона ${defender}")
        }
        if (attackResult.wereAllCreaturesInDefenderSquadKilled) {
          println(s"Все $defender были убиты")
          if (defenderArmy.isNotAlive) {
            return if (attackerArmy == firstArmy) BattleResult.FIRST_WIN else BattleResult.SECOND_WIN
          }
        }
      }
    }
    throw new RuntimeException("Incorrect place in battle method")
  }


  private val attackModifierCoeficient = 0.05
  private val scale = 2

  /**
    * Расчет коэффициента атаки.
    *
    * Если защита защищающегося больше атаки атакующего, то идет округление вниз до двух знаков после запятой.
    *
    * @param attacker
    * @param attackerHero
    * @param defender
    * @param defenderHero
    * @return
    */
  private def calculateAttackModifier(attacker: Squad, attackerHero: Hero, defender: Squad, defenderHero: Hero): Double = {
    val attack = attacker.getAttack + attackerHero.attack
    val defence = defender.getDefense + defenderHero.defense
    if (attack >= defence) 1 + (attack - defence) * attackModifierCoeficient
    else BigDecimal(1 / (1 + (defence - attack) * attackModifierCoeficient)).setScale(scale, BigDecimal.RoundingMode.HALF_DOWN).toDouble
  }

  private def placeSquadsOnField() = {
    for (i <- firstArmy.squadsInArmy.indices) {
      field.placeSquadOnField(firstArmy.squadsInArmy(i), i, firstArmy.squadsInArmy.length, true)
    }
    for (i <- secondArmy.squadsInArmy.indices) {
      field.placeSquadOnField(secondArmy.squadsInArmy(i), i, firstArmy.squadsInArmy.length, false)
    }
  }

}
