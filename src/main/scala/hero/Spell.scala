package hero

import main.Squad

trait HeroSpell {
  def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean

  def squadSpellByHeroSpell(goal: Squad): Spell

  def toString: String
}

abstract class AllySpell extends HeroSpell {
  override def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean = goal.army == hero.army
}

object StoneSpellHeroSpell extends AllySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new StoneSpellSkill(goal)

  override def toString: String = "Каменная кожа"
}

abstract class EnemySpell extends HeroSpell {
  override def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean = goal.army != hero.army
}

object DestructionSpellHeroSpell extends EnemySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new DestructionSpell(goal)

  override def toString: String = "Разрушение брони"
}


trait Spell {
}

abstract class ContinuusSpell(private val name: String, private val roundsToBe: Int, private val goal : Squad) extends Spell {
  private var remainRounds = roundsToBe

  def decreaseSteps() = {
    remainRounds = remainRounds - 1
    if (remainRounds == 0) {
      endSpell()
    }
  }

  private def endSpell() = {
    goal.removeSpell(this)
  }

  override def toString: String = name
}

abstract class Buff(private val name: String, private val roundsToBe: Int, private val goal : Squad) extends ContinuusSpell(name, roundsToBe, goal) {
}

abstract class Debuff(private val name: String, private val roundsToBe: Int, private val goal : Squad) extends ContinuusSpell(name, roundsToBe, goal) {
}

//Заклинания на изменение защиты отряда
trait DefenseSpell {
  def changeDefenseValue: Int
}

class StoneSpellSkill(private val goal : Squad) extends Buff("Каменная кожа", 3, goal) with DefenseSpell {
  override def changeDefenseValue: Int = 3
}

class DestructionSpell(private val goal : Squad) extends Debuff("Разрушение брони", 3, goal) with DefenseSpell {
  override def changeDefenseValue: Int = -3
}