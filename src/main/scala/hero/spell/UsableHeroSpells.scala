package hero.spell

import hero.{Hero, HeroInArmy}
import main.SquadInArmy

class StoneSkinHeroSpell extends ContinuousHeroSpell {
  override def use(wizard: Hero): Spell = new StoneSkinSpell(roundsOfSpell(wizard))

  override def manacost: Int = 4

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromSameArmy(goal, hero)

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Каменная кожа"
}

class DestructionHeroSpell extends ContinuousHeroSpell {
  override def use(wizard: Hero): Spell = new DestructionSpell(roundsOfSpell(wizard))

  override def manacost: Int = 5

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Разрушение брони"
}

class BloodLustHeroSpell extends ContinuousHeroSpell {
  override def use(wizard: Hero): Spell = new BloodLustSpell(roundsOfSpell(wizard))

  override def manacost: Int = 5

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromSameArmy(goal, hero)

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Жажда крови"
}

class WeaknessHeroSpell extends ContinuousHeroSpell {
  override def use(wizard: Hero): Spell = new WeaknessSpell(roundsOfSpell(wizard))

  override def manacost: Int = 6

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromDifArmies(goal, hero)

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Слабость"
}

class MagicMissileHeroSpell extends StraightDamageHeroSpell {
  override protected def basicDamage: Int = 10

  override protected def damageCoefficient: Int = 10

  override def use(wizard: Hero): Spell = new MagicMissileSpell(damage(wizard))

  override def manacost: Int = 5

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Волшебная стрела"
}

class HealingHeroSpell extends HeroSpell {
  override def use(wizard: Hero): Spell = new HealingSpell(healing(wizard))

  override def manacost: Int = 5

  override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean = areSquadAndHeroFromSameArmy(goal, hero) && goal.hasNoMaxHealth

  /**
    * Расчет урона от заклинания прямого урона = базовый урон + (сила магии героя * коэффициент заклинания)
    * @param wizard Атакующий герой
    * @return
    */
  private def healing(wizard: Hero) : Int = basicHealing + wizard.sorcery * healingCoefficient

  private def basicHealing: Int = 10

  private def healingCoefficient: Int = 5

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Лечение"
}