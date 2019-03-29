package hero

import main.{Squad, SquadInArmy}

abstract class Spell(title: String, private val manaForSpell: Int) {
  override def toString: String = title

  def manacost = manaForSpell

  def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean

  protected final def areSquadAndHeroFromSameArmy(goal: SquadInArmy, hero: HeroInArmy) = goal.army == hero.army

  protected final def areSquadAndHeroFromDifArmies(goal: SquadInArmy, hero: HeroInArmy) = !areSquadAndHeroFromSameArmy(goal, hero)
}

abstract class StraightDamageSpell(private val title: String, private val manaForSpell: Int) extends Spell(title, manaForSpell) {

  def damage: Int

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)
}

object HealingSpell extends Spell("Лечение", 5) {
  def healing = 20

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean =
    areSquadAndHeroFromSameArmy(goal, hero) && goal.hasNoMaxHealth
}

object MagicMissileSpell extends StraightDamageSpell("Волшебная стрела", 4) {
  override def damage: Int = 30
}

abstract class ContinuousSpell(private val name: String, private val manaForSpell: Int, private val roundsToBe: Int) extends Spell(name, manaForSpell) {
  private var remainRounds = roundsToBe

  def decreaseSteps(goal: Squad) = {
    remainRounds = remainRounds - 1
    if (remainRounds == 0) {
      endSpell(goal)
    }
  }

  private def endSpell(goal: Squad) = {
    goal.removeSpell(this)
  }
}

trait AttackSpell extends Spell {
  def changeAttackValue: Int
}

//Заклинания на изменение защиты отряда
trait DefenseSpell extends Spell {
  def changeDefenseValue: Int
}

object StoneSkinSkill extends ContinuousSpell("Каменная кожа", 4, 3) with DefenseSpell {
  override def changeDefenseValue: Int = 3

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromSameArmy(goal, hero)
}


object DestructionSpell extends ContinuousSpell("Разрушение брони", 5,3) with DefenseSpell {
  override def changeDefenseValue: Int = -3

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)
}

object BloodLustSpell extends ContinuousSpell("Жажда крови", 3, 4) with AttackSpell {
  override def changeAttackValue: Int = 3

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromSameArmy(goal, hero)
}

object WeaknessSpell extends ContinuousSpell("Слабость", 3, 6) with AttackSpell {
  override def changeAttackValue: Int = -3

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)
}


//trait HeroSpell {
//  def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean
//
//  def squadSpellByHeroSpell(goal: Squad): Spell
//
//  def toString: String
//}
//
//abstract class AllySpell extends HeroSpell {
//  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = goal.army == hero.army
//}
//
//object StoneSkinHeroSpell extends AllySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new StoneSkinSkill(goal)
//
//  override def toString: String = "Каменная кожа"
//}
//
//object BloodLustHeroSpell extends AllySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new BloodLustSpell(goal)
//
//  override def toString: String = "Жажда крови"
//}
//
//object HealingHeroSpell extends AllySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new HealingSpell()
//
//  override def toString: String = "Лечение"
//}
//
//abstract class EnemySpell extends HeroSpell {
//  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = goal.army != hero.army
//}
//
//object DestructionHeroSpell extends EnemySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new DestructionSpell(goal)
//
//  override def toString: String = "Разрушение брони"
//}
//
//object WeaknessHeroSpell extends EnemySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new WeaknessSpell(goal)
//
//  override def toString: String = "Слабость"
//}
//
//object MagicMissileHeroSpell extends EnemySpell {
//  override def squadSpellByHeroSpell(goal: Squad): Spell = new MagicMissileSpell()
//
//  override def toString: String = "Волшебная стрела"
//}
//
//
//trait Spell {
//  def manaCost: Int
//}
//
//abstract class StraightDamageSpell(private val name: String) extends Spell {
//  def damage: Int
//
//  override def toString: String = name
//}
//
//class HealingSpell(private val name: String = "Лечение") extends Spell {
//  def healing = 20
//
//  override def toString: String = name
//}
//
//class MagicMissileSpell extends StraightDamageSpell("Волшебная стрела") {
//  override def damage: Int = 30
//}
//
//abstract class ContinuousSpell(private val name: String, private val roundsToBe: Int, private val goal: Squad) extends Spell {
//  private var remainRounds = roundsToBe
//
//  def decreaseSteps() = {
//    remainRounds = remainRounds - 1
//    if (remainRounds == 0) {
//      endSpell()
//    }
//  }
//
//  private def endSpell() = {
//    goal.removeSpell(this)
//  }
//
//  override def toString: String = name
//}
//
//abstract class Buff(private val name: String, private val roundsToBe: Int, private val goal: Squad) extends ContinuousSpell(name, roundsToBe, goal) {
//}
//
//abstract class Debuff(private val name: String, private val roundsToBe: Int, private val goal: Squad) extends ContinuousSpell(name, roundsToBe, goal) {
//}
//
//trait AttackSpell extends Spell {
//  def changeAttackValue: Int
//}
//
////Заклинания на изменение защиты отряда
//trait DefenseSpell extends Spell {
//  def changeDefenseValue: Int
//}
//
//class StoneSkinSkill(private val goal: Squad) extends Buff("Каменная кожа", 3, goal) with DefenseSpell {
//  override def changeDefenseValue: Int = 3
//}
//
//class DestructionSpell(private val goal: Squad) extends Debuff("Разрушение брони", 3, goal) with DefenseSpell {
//  override def changeDefenseValue: Int = -3
//}
//
//class BloodLustSpell(private val goal: Squad) extends Buff("Жажда крови", 3, goal) with AttackSpell {
//  override def changeAttackValue: Int = 3
//}
//
//class WeaknessSpell(private val goal: Squad) extends Buff("Слабость", 3, goal) with AttackSpell {
//  override def changeAttackValue: Int = -3
//}