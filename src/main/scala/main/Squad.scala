package main

import hero.{ContinuusSpell, DefenseSpell, AttackSpell, Spell}

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

  def getAttack: Int = attack + spellsOnSquad.collect {
    case attackSpell: AttackSpell => attackSpell.changeAttackValue
    case _ => 0
  }.sum

  def getDefense: Int = defence + spellsOnSquad.collect {
    case defenseSpell: DefenseSpell => defenseSpell.changeDefenseValue
    case _ => 0
  }.sum

  def receiveSpell(spell: Spell) = {
    spell match {
      case continuusSpell: ContinuusSpell => spellsOnSquad += continuusSpell
      //если моментальный - то recieveDamage...
    }
  }

  def removeSpell(spell: ContinuusSpell): Unit = {
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

  def areSquadsFromTheSameArmy(other: Squad) = army == other.army

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
      return new SquadAttackResult(damage, true, killedCreatures, currentCreaturesNumber)
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
    new SquadAttackResult(damage, false, killedCreatures, currentCreaturesNumber)
  }

  def totalHealth() = maxHealth * (currentCreaturesNumber - 1) + currentHealth

  private def wouldSquadBeDeadAfterDamage(damage: Int): Boolean = totalHealth() <= damage
}
