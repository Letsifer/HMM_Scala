package main

import hero.{ContinuusSpell, DefenseSpell, HeroSpell, Spell}

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Attacker {
  def getAttack: Int

  def getDefense: Int
}

class Squad(val name: String, val creaturesInSquadAtStart: Int, private val maxHealth: Int, private val minAttack: Int, private val maxAttack: Int,
            private val attack: Int, private val defence: Int, val speed: Int, val army: Army) extends Attacker {

  private val spellsOnSquad = new ListBuffer[ContinuusSpell]

  private var currentCreaturesNumber = creaturesInSquadAtStart
  private var currentHealth = maxHealth

  def getAttack: Int = attack

  def getDefense: Int = defence + spellsOnSquad.collect{
    case defenseSpell: DefenseSpell => defenseSpell.changeDefenseValue
    case _ => 0
  }.sum

  def recieveSpell(spell: Spell) = {
    spell match {
      case continuusSpell: ContinuusSpell => spellsOnSquad += continuusSpell
        //если моментальный - то recieveDamage...
    }
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

  def areSquadsFromTheSameArmy(other: Squad) = army == other.army

  override def toString: String = name

  private def countAttack = {
    val randomize = new Random()
    Range(0, currentCreaturesNumber).map(i => minAttack + randomize.nextInt(maxAttack - minAttack + 1)).sum
  }

  private def receiveDamage(damage: Int): SquadAttackResult = {
    if (wouldSquadBeDeadAfterDamage(damage)) {
      val killedCreatures = currentCreaturesNumber
      currentCreaturesNumber = 0
      currentHealth = 0
      return new SquadAttackResult(damage, true, killedCreatures, currentCreaturesNumber)
    }
    var killedCreatures = damage / maxHealth
    val remainDamage = damage - killedCreatures * maxHealth
    if (currentHealth > remainDamage) {
      currentHealth -= remainDamage
    } else {
      killedCreatures += 1
      currentHealth = maxHealth - (currentHealth - remainDamage)
    }
    currentCreaturesNumber -= killedCreatures
    new SquadAttackResult(damage, false, killedCreatures, currentCreaturesNumber)
  }

  def totalHealth() = maxHealth * (currentCreaturesNumber - 1) + currentHealth

  private def wouldSquadBeDeadAfterDamage(damage: Int): Boolean = totalHealth() <= damage
}