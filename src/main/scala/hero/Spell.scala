package hero

import main.Squad

trait HeroSpell {
  def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean

  def squadSpellByHeroSpell: Spell

  def toString: String
}

abstract class AllySpell extends HeroSpell {
  override def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean = goal.army == hero.army
}

object StoneSpellHeroSpell extends AllySpell {
  override def squadSpellByHeroSpell: Spell = new StoneSpellSkill

  override def toString: String = "Каменная кожа"
}

abstract class EnemySpell extends HeroSpell {
  override def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean = goal.army != hero.army
}

object DestructionSpellHeroSpell extends EnemySpell {
  override def squadSpellByHeroSpell: Spell = new DestructionSpell

  override def toString: String = "Разрушение брони"
}


trait Spell {
}

abstract class ContinuusSpell(private val name: String, private val roundsToBe: Int) extends Spell {
  private var remainRounds = roundsToBe

  def decreaseSteps() = {
    remainRounds = remainRounds - 1
    if (remainRounds == 0) {
      endSpell()
    }
  }

  private def endSpell() = {

  }

  override def toString: String = name
}

abstract class Buff(private val name: String, private val roundsToBe: Int) extends ContinuusSpell(name, roundsToBe) {
}

abstract class Debuff(private val name: String, private val roundsToBe: Int) extends ContinuusSpell(name, roundsToBe) {
}

//Заклинания на изменение защиты отряда
trait DefenseSpell {
  def changeDefenseValue: Int
}

class StoneSpellSkill extends Buff("Каменная кожа", 3) with DefenseSpell {
  override def changeDefenseValue: Int = 3
}

class DestructionSpell extends Debuff("Разрушение брони", 3) with DefenseSpell {
  override def changeDefenseValue: Int = -3
}