package hero

import main.Army

import scala.util.Random

class HeroInArmy(val hero: Hero, val army: Army) {

  def updateSpellUsage = hero.updateSpellUsage

  def useSpellOnSquad(enemyArmy: Army) = {
    if (hero.noSpellInRound) {
      val chosenSpell = hero.chooseSpell
      val randomAllySquad = army.getRandomAliveSquad
      if (chosenSpell.canBeActedOnSquad(randomAllySquad, this)) {
        println(randomAllySquad.receiveSpell(chosenSpell.squadSpellByHeroSpell(randomAllySquad.squad), hero))
        hero.useSpell
      } else {
        val randomEnemySquad = enemyArmy.getRandomAliveSquad
        if (chosenSpell.canBeActedOnSquad(randomEnemySquad, this)) {
          println(randomEnemySquad.receiveSpell(chosenSpell.squadSpellByHeroSpell(randomEnemySquad.squad), hero))
          hero.useSpell
        }
      }
    }
  }
}

class HeroSpellBook {
  private val heroSpells = List(StoneSkinHeroSpell, DestructionHeroSpell, WeaknessHeroSpell, BloodLustHeroSpell, MagicMissileHeroSpell, HealingHeroSpell)

  def chooseSpell: HeroSpell = {
    val rand = new Random()
    heroSpells(rand.nextInt(heroSpells.size))
  }
}

class Hero(val name: String, val attack: Int, val defense: Int) {

  private val spellBook = new HeroSpellBook
  private var canUseSpell = true

  def noSpellInRound = canUseSpell

  def useSpell = canUseSpell = false

  def updateSpellUsage = canUseSpell = true

  def chooseSpell = spellBook.chooseSpell

  override def toString: String = name

}
