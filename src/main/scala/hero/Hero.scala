package hero

import main.Army

import scala.util.Random

class HeroBean(val name: String, val attack: Int, val defense: Int)

class HeroSpellBook {
  private val heroSpells = List(StoneSpellHeroSpell, DestructionSpellHeroSpell)

  def chooseSpell: HeroSpell = {
    val rand = new Random()
    heroSpells(rand.nextInt(heroSpells.size))
  }
}

class Hero(val name: String, val attack: Int, val defense: Int, val army: Army) {

  private val spellBook = new HeroSpellBook
  private var canUseSpell = true

  def updateSpellUsage = canUseSpell = true

  def useSpellOnSquad(allyArmy: Army, enemyArmy: Army) : Unit = {
    if (!canUseSpell) {
      return
    }
    val chosenSpell = spellBook.chooseSpell
    val randomAllySquad = allyArmy.getRandomAliveSquad
    if (chosenSpell.canBeActedOnSquad(randomAllySquad, this)) {
      randomAllySquad.receiveSpell(chosenSpell.squadSpellByHeroSpell(randomAllySquad))
      println(s"$name использует заклинание $chosenSpell на $randomAllySquad")
      canUseSpell = false
    } else {
      val randomEnemySquad = enemyArmy.getRandomAliveSquad
      if (chosenSpell.canBeActedOnSquad(randomEnemySquad, this)) {
        randomEnemySquad.receiveSpell(chosenSpell.squadSpellByHeroSpell(randomAllySquad))
        println(s"$name использует заклинание $chosenSpell на $randomEnemySquad")
        canUseSpell = false
      }
    }
  }
}
