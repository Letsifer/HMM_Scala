package spell

import main.{Squad, Hero}

trait Spell {
  def action(goal: Squad, hero : Hero)
  def canBeActedOnSquad(goal : Squad)
}

abstract class ContinuusSpell(private val roundsToBe : Int) extends Spell {
  private var remainRounds = roundsToBe

  override def action(goal: Squad, hero : Hero): Unit = {
    //здесь будет указано наложение бафа\дебафа на отряд, т.е. добавление бафа\дебафа в список таких заклинаний
    goal.spellsOnSquad += this
  }

  abstract def continuusReaction(goal: Squad, hero : Hero)

  def decreaseSteps() = {
    remainRounds = remainRounds - 1
    if (remainRounds == 0) {
      endSpell()
    }
  }

  private def endSpell() = {

  }
}

abstract  class Buff(private val roundsToBe : Int) extends ContinuusSpell(roundsToBe) {
  override def canBeActedOnSquad(goal: Squad): Unit = {

  }
}

abstract  class Debuff(private val roundsToBe : Int) extends ContinuusSpell(roundsToBe) {
  override def canBeActedOnSquad(goal: Squad): Unit = {

  }
}

//Заклинания на изменение защиты отряда
trait DefenseSpell {
  def changeDefenseValue : Int
}

object StoneSpellSkill extends Buff(3) with DefenseSpell {
  override def changeDefenseValue: Int = 3

  override def continuusReaction(goal: Squad, hero: Hero): Unit = goal.defence += changeDefenseValue
}

object DestructionSpell extends Debuff(3) with DefenseSpell {
  override def changeDefenseValue: Int = -3
}