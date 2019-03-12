import scala.collection.mutable.Queue

class Process(private val firstArmy : Army, private val secondArmy : Army) {

  private var queue = new Queue[Squad]

  def battle() : BattleResult.BattleResult = {
    while (firstArmy.isAlive() && secondArmy.isAlive()) {
      println("------Следующий ход------")
      queue = queue ++= firstArmy.squads
      queue = queue ++= secondArmy.squads
      queue = queue.sortWith(_.speed > _.speed)
      while (queue.size > 0) {
        val attacker = chooseNextAttacker()
        val attackerArmy = attacker.army
        val defenderArmy = if (attackerArmy == firstArmy) secondArmy else firstArmy
        val defender = chooseAttackGoal(defenderArmy)
        val attackResult = attacker.attack(defender, calculateAttackModifier(attacker, attackerArmy.hero, defender, defenderArmy.hero))
        if (attackResult.killedCreatures > 0) {
          println(s"${attacker.name} нанесли ${attackResult.resultDamage} урона, убито ${attackResult.killedCreatures} ${defender.name}")
        } else {
          println(s"${attacker.name} нанесли ${attackResult.resultDamage} урона")
        }
        if (attackResult.wereAllCreaturesInDefenderSquadKilled) {
          if (defenderArmy.isNotAlive()) {
            if (attackerArmy == firstArmy) return BattleResult.FIRST_WIN
            else return BattleResult.SECOND_WIN
          }
        }
      }
    }
    throw new RuntimeException("Incorrect place in battle method")
  }

  private val attackModifierCoeficient = 0.05

  private def calculateAttackModifier(attacker: Squad, attackerHero : Hero, defender: Squad, defenderHero: Hero): Double = {
    val attack = attacker.attack + attackerHero.attack
    val defence = defender.defence + defenderHero.defense
    if (attack >= defence) 1 + (attack - defence) * attackModifierCoeficient
    else 1 / (1 + (defence - attack) * attackModifierCoeficient)
  }

  private def chooseNextAttacker() : Squad = {
    while (queue.size > 0) {
      val attacker =  queue.dequeue
      if (attacker.isAlive()) {
        return attacker
      }
    }
    throw new NoSuchElementException("No alive creature in process queue")
  }

  private def chooseAttackGoal(defenderArmy: Army): Squad = defenderArmy.squads.filter(_.isAlive()).headOption.get

}
