package main

class SquadAttackResult(val resultDamage: Int, val wereAllCreaturesInDefenderSquadKilled: Boolean, val killedCreatures: Int, val remainCreatures: Int) {
  def areCreaturesKilled() = killedCreatures > 0
}


