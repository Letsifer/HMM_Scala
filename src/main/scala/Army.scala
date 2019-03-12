class Army(val name: String, val hero: Hero, private val squads: List[Squad]) {

  private var index = squads.size - 1

  def isAlive(): Boolean = squads.exists(_.isAlive())

  def action(defenderArmy: Army): ArmyAttackResult = {
    val attacker = chooseNextAttacker()
    if (attacker == EmptySquad) return NobodyToArmyAttack
    val defender = chooseAttackGoal(defenderArmy)
    if (defender == EmptySquad) return NobodyToArmyAttack
    new ArmyAttackResult(attacker, defender, attacker.attack(defender, calculateAttackModifier(attacker, defender, defenderArmy.hero)))
  }

  private val attackModifierCoeficient = 0.05

  private def calculateAttackModifier(attacker: Squad, defender: Squad, defenderHero: Hero): Double = {
    val attack = attacker.attack + hero.attack
    val defence = defender.defence + defenderHero.defense
    if (attack >= defence) 1 + (attack - defence) * attackModifierCoeficient
    else 1 / (1 + (defence - attack) * attackModifierCoeficient)
  }

  private def chooseNextAttacker(): Squad = {
    val indexAtStart = index + 1
    while (index != indexAtStart) {
      if (index >= squads.size - 1) index = 0
      else index += 1
      if (squads(index).isAlive()) return squads(index)
    }
    return EmptySquad
  }

  private def chooseAttackGoal(defenderArmy: Army): Squad = defenderArmy.squads.filter(_.isAlive()).headOption.getOrElse(EmptySquad)

}
