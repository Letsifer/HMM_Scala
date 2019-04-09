package main

import hero.Hero
import hero.spell._

import scala.collection.mutable.ListBuffer
import scala.util.Random

class SquadInArmy(val squad: Squad, val army: Army) {

  def areSquadsFromTheSameArmy(other: SquadInArmy) = army == other.army

  def isAlive = squad.isAlive

  def getShortTitle = if (isAlive) squad.toString.take(1) else " "

  def getSpeed = squad.speed

  def receiveSpell(spell: Spell, wizard : Hero) = squad.receiveSpell(spell, wizard)

  def attack(defender: SquadInArmy, attackModifier: Double) = squad.attack(defender.squad, attackModifier)

  override def toString: String = squad.toString

  def totalHealth = squad.totalHealth()

  def hasNoMaxHealth = squad.hasNoMaxHealth
}

class Squad(val name: String, val creaturesInSquadAtStart: Int, private val maxHealth: Int, private val minAttack: Int, private val maxAttack: Int,
            private val attack: Int, private val defence: Int, val speed: Int)  {

  private val spellsOnSquad = new ListBuffer[ContinuousSpell]

  private var currentCreaturesNumber = creaturesInSquadAtStart
  private var currentHealth = maxHealth

  def hasNoMaxHealth = currentHealth != maxHealth

  def getCurrentHealth = currentHealth

  def getAttack: Int = attack + spellsOnSquad.collect {
    case attackSpell: AttackSpell => attackSpell.changeAttackValue
    case _ => 0
  }.sum

  def getDefense: Int = defence + spellsOnSquad.collect {
    case defenseSpell: DefenseSpell => defenseSpell.changeDefenseValue
    case _ => 0
  }.sum

  def receiveSpell(spell: Spell, wizard: Hero) = {
    spell match {
      case continuousSpell: ContinuousSpell => {
        spellsOnSquad += continuousSpell
        new HeroSpellResult(wizard, spell, this)
      }
      case straightDamageSpell: StraightDamageSpell => {
        val result = receiveDamage(straightDamageSpell.damage)
        new DamageSpellResult(wizard, spell, this, result.resultDamage, result.wereAllCreaturesInDefenderSquadKilled, result.killedCreatures)
      }
      case healingSpell: HealingSpell => {
        if (maxHealth - currentHealth >= healingSpell.healing) {
          currentHealth += healingSpell.healing
          new HealingSpellResult(wizard, spell, this, healingSpell.healing)
        } else {
          val healed = maxHealth - currentHealth
          currentHealth = maxHealth
          new HealingSpellResult(wizard, spell, this, healed)
        }
      }
    }
  }

  def updateSquad = {
    spellsOnSquad
      .filter(spell => spell.decreaseRounds)
      .foreach(spell => removeSpell(spell))
  }

  def removeSpell(spell: ContinuousSpell): Unit = {
    spellsOnSquad -= spell
  }

  /**
    * Атака защищающегося юнита
    *
    * @param defender Защищающийся отряд
    * @return Был ли убит защищающийся отряд
    */
  def attack(defender: Squad, attackModifier: Double): SquadAttackResult = {
    val damage = (countAttack * attackModifier).toInt
    defender.receiveDamage(damage)
  }

  def getLostCreaturesNumberWhileBattle = creaturesInSquadAtStart - currentCreaturesNumber

  def isAlive = currentCreaturesNumber > 0

  override def toString: String = name

  def getFullInfo: String = {
    val squadBuilder = StringBuilder.newBuilder
    squadBuilder.append(s"$name: HP=$currentHealth($maxHealth), количество=$currentCreaturesNumber (было $creaturesInSquadAtStart)\n")
    squadBuilder.append(s"Атака=$getAttack, защита=$getDefense, урон=($minAttack-$maxAttack), скорость=$speed\n")
    for (spell <- spellsOnSquad) {
      squadBuilder.append(s"$spell\n")
    }
    squadBuilder.toString
  }

  private def countAttack = {
    val randomize = new Random()
    currentCreaturesNumber * (minAttack + randomize.nextInt(maxAttack - minAttack + 1))
  }

  private def receiveDamage(damage: Int): SquadAttackResult = {
    if (wouldSquadBeDeadAfterDamage(damage)) {
      val killedCreatures = currentCreaturesNumber
      currentCreaturesNumber = 0
      currentHealth = 0
      return new SquadAttackResult(damage, true, killedCreatures)
    }
    var killedCreatures = damage / maxHealth
    val remainDamage = damage - killedCreatures * maxHealth
    if (currentHealth > remainDamage) {
      currentHealth -= remainDamage
    } else {
      killedCreatures += 1
      currentHealth = maxHealth - (remainDamage - currentHealth)
    }
    currentCreaturesNumber -= killedCreatures
    new SquadAttackResult(damage, false, killedCreatures)
  }

  def totalHealth() = maxHealth * (currentCreaturesNumber - 1) + currentHealth

  private def wouldSquadBeDeadAfterDamage(damage: Int): Boolean = totalHealth() <= damage
}
