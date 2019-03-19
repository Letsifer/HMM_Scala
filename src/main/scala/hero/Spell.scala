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

object StoneSkinHeroSpell extends AllySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new StoneSkinSkill(goal)

  override def toString: String = "Каменная кожа"
}

object BloodLustHeroSpell extends AllySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new BloodLustSpell(goal)

  override def toString: String = "Жажда крови"
}

//object HealingHeroSpell extends AllySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new HealingSpell()
//
//  override def toString: String = "Лечение"
//}

abstract class EnemySpell extends HeroSpell {
  override def canBeActedOnSquad(goal: Squad, hero: Hero): Boolean = goal.army != hero.army
}

object DestructionHeroSpell extends EnemySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new DestructionSpell(goal)

  override def toString: String = "Разрушение брони"
}

object WeaknessHeroSpell extends EnemySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new WeaknessSpell(goal)

  override def toString: String = "Слабость"
}

object MagicMissleHeroSpell extends EnemySpell {
  override def squadSpellByHeroSpell(goal: Squad): Spell = new MagicMissleSpell()

  override def toString: String = "Волшебная стрела"
}


trait Spell {
}

abstract class StraightDamageSpell(private val name: String) extends Spell{
  def damage : Int
}

//class HealingSpell(private val name = "Лечение") extends Spell {
//  def healing = 20
//}

class MagicMissleSpell extends StraightDamageSpell("Волшебная стрела") {
  override def damage: Int = 30
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

trait AttackSpell {
  def changeAttackValue: Int
}

//Заклинания на изменение защиты отряда
trait DefenseSpell {
  def changeDefenseValue: Int
}

class StoneSkinSkill(private val goal : Squad) extends Buff("Каменная кожа", 3, goal) with DefenseSpell {
  override def changeDefenseValue: Int = 3
}

class DestructionSpell(private val goal : Squad) extends Debuff("Разрушение брони", 3, goal) with DefenseSpell {
  override def changeDefenseValue: Int = -3
}

class BloodLustSpell(private val goal : Squad) extends Buff("Жажда крови", 3, goal) with AttackSpell {
  override def changeAttackValue: Int = 3
}

class WeaknessSpell(private val goal : Squad) extends Buff("Слабость", 3, goal) with AttackSpell {
  override def changeAttackValue: Int = -3
}