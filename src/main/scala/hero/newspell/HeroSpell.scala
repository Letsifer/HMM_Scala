package hero.newspell

import hero.HeroInArmy
import main.SquadInArmy

abstract class HeroSpell[S <: Spell] {
  def use() : S

  def manacost : Int

  def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy): Boolean

  protected final def areSquadAndHeroFromSameArmy(goal: SquadInArmy, hero: HeroInArmy) = goal.army == hero.army

  protected final def areSquadAndHeroFromDifArmies(goal: SquadInArmy, hero: HeroInArmy) = !areSquadAndHeroFromSameArmy(goal, hero)
}
