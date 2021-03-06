package hero

import hero.spell._
import main.{Army, Squad}

import scala.util.Random

class HeroInArmy(val hero: Hero, val army: Army) {

  def useSpellOnSquad(enemyArmy: Army) = {
    if (hero.noSpellInRound) {
      val chosenSpell = hero.chooseSpell
      if (chosenSpell.nonEmpty) {
        val randomAllySquad = army.getRandomAliveSquad
        if (chosenSpell.get.canBeActedOnSquad(randomAllySquad, this)) {
          println(hero.useSpell(chosenSpell.get, randomAllySquad.squad))
        } else {
          val randomEnemySquad = enemyArmy.getRandomAliveSquad
          if (chosenSpell.get.canBeActedOnSquad(randomEnemySquad, this)) {
            println(hero.useSpell(chosenSpell.get, randomEnemySquad.squad))
          }
        }
      }
    }
  }

  def getFullInfo = hero.getFullInfo
}

class HeroSpellBook {
  private val heroSpells = List[HeroSpell](new StoneSkinHeroSpell(), new DestructionHeroSpell(), new BloodLustHeroSpell(), new WeaknessHeroSpell(), new MagicMissileHeroSpell(), new HealingHeroSpell())

  def chooseSpell(heroMana: Int): Option[HeroSpell] = {
    val rand = new Random()
    val spellsWithManacostLessThenCurrentMana = heroSpells.filter(_.manacost <= heroMana)
    if (spellsWithManacostLessThenCurrentMana.nonEmpty) {
      Option(spellsWithManacostLessThenCurrentMana(rand.nextInt(spellsWithManacostLessThenCurrentMana.size)))
    } else {
      Option.empty
    }
  }
}

/**
  * Создания героя по имени с базовыми характеристиками.
  *
  * @param name
  * @param attack
  * @param defense
  * @param sorcery   Сила магии
  * @param knowledge Колдовство
  */
class Hero(val name: String, val attack: Int, val defense: Int, val sorcery: Int, val knowledge: Int) {

  private val spellBook = new HeroSpellBook
  private var canUseSpell = true

  private val maxMana = 10 * (knowledge + 1)
  private var mana = maxMana

  def noSpellInRound = canUseSpell

  def useSpell(spell: HeroSpell, squad: Squad) = {
    canUseSpell = false
    mana -= spell.manacost
    squad.receiveSpell(spell.use(this), this)
  }

  def updateSpellUsage = canUseSpell = true

  def chooseSpell = spellBook.chooseSpell(mana)

  override def toString: String = name

  def getFullInfo = s"$name: Мана: $mana ($maxMana)"

}
