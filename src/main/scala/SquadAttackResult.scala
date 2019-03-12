class ArmyAttackResult(val attacker: Squad, val defender: Squad, val squadAttackResult: SquadAttackResult) {
  def getKilledCreatures() = squadAttackResult.killedCreatures

  def getDamage() = squadAttackResult.resultDamage
}

class SquadAttackResult(val resultDamage: Int, val wereAllKilled: Boolean, val killedCreatures: Int, val remainCreatures: Int)

object NobodyToSquadAttack extends SquadAttackResult(0, false, 0, 0)

object NobodyToArmyAttack extends ArmyAttackResult(EmptySquad, EmptySquad, NobodyToSquadAttack)
