package hero.spell

import hero.{Hero, HeroInArmy}
import main.SquadInArmy

abstract class HeroSpell {

  def use(wizard: Hero) : Spell

  def manacost : Int

  def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  def getTitle: String

  override def toString: String = getTitle

  protected final def areSquadAndHeroFromSameArmy(goal: SquadInArmy, hero: HeroInArmy) = goal.army == hero.army

  protected final def areSquadAndHeroFromDifArmies(goal: SquadInArmy, hero: HeroInArmy) = !areSquadAndHeroFromSameArmy(goal, hero)
}

abstract class StraightDamageHeroSpell extends HeroSpell {

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



abstract class ContinuousHeroSpell extends HeroSpell {
  def roundsOfSpell(wizard : Hero) = Math.max(wizard.sorcery, 1)
}

