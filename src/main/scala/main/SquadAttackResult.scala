package main

import hero.{Hero, Spell}

class SquadAttackResult(val resultDamage: Int, val wereAllCreaturesInDefenderSquadKilled: Boolean, val killedCreatures: Int) {
  def areCreaturesKilled() = killedCreatures > 0
}

class HeroSpellResult(private val wizard: Hero, private val usedSpell: Spell, private val goal: Squad) {
  override def toString: String = (s"$wizard использовал заклинание $usedSpell на $goal")
}

class DamageSpellResult(val wizard: Hero, val usedSpell: Spell, val goal: Squad,
                        val resultDamage: Int, val wereAllCreaturesInDefenderSquadKilled: Boolean, val killedCreatures: Int)
  extends HeroSpellResult(wizard, usedSpell, goal) {
  override def toString: String = {
    val basicMessage = super.toString
    val damageMessage = if (killedCreatures > 0)
      (s"$goal получили ${resultDamage} урона, убито ${killedCreatures} $goal")
    else
      (s"$goal получили ${resultDamage} урона")
    basicMessage + "\n" + damageMessage
  }
}

class HealingSpellResult(val wizard: Hero, val usedSpell: Spell, val goal: Squad, val resultHealing: Int) extends HeroSpellResult(wizard, usedSpell, goal) {
  override def toString: String = {
    val basicMessage = super.toString
    val healingMessage = (s"$goal восстановили ${resultHealing} здоровья")
    basicMessage + "\n" + healingMessage
  }
}


