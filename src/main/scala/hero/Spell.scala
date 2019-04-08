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

  /**
    * Расчет урона от заклинания прямого урона = базовый урон + (сила магии героя * коэффициент заклинания)
    * @param wizard Атакующий герой
    * @return
    */
  def damage(wizard: Hero) : Int = basicDamage + wizard.sorcery * damageCoefficient

  protected def basicDamage: Int

  protected def damageCoefficient: Int

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)
}

object HealingSpell extends Spell("Лечение", 5) {
  def healing(wizard : Hero) = basicHealing + wizard.sorcery * healingCoefficient

  private def basicHealing = 10

  private def healingCoefficient = 5

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean =
    areSquadAndHeroFromSameArmy(goal, hero) && goal.hasNoMaxHealth
}

object MagicMissileSpell extends StraightDamageSpell("Волшебная стрела", 5) {
  override def basicDamage: Int = 10

  override def damageCoefficient: Int = 10
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