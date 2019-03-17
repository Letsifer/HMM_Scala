package main

class SquadBean(val name: String, val creaturesInSquadAtStart: Int, val maxHealth: Int, val minAttack: Int, val maxAttack: Int,
                val attack: Int, val defence: Int, val speed : Int) extends Attacker {

  def getAttack : Int = attack
  def getDefense : Int = defence
}
