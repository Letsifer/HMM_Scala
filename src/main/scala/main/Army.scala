package main

import hero.{Hero, HeroInArmy}

import scala.util.Random

class Army(val name: String, private val hero: Hero, private val squads : List[Squad]) {

  val heroInArmy = new HeroInArmy(hero, this)
  val squadsInArmy: List[SquadInArmy] = squads.map(squad => new SquadInArmy(squad, this))

  override def toString: String = name

  def isAlive = squads.exists(_.isAlive)
  def isNotAlive = !isAlive

  def getBattleResult = {
    squads.map(squad => s"$squad: Было ${squad.creaturesInSquadAtStart}, потеряно ${squad.getLostCreaturesNumberWhileBattle}").mkString("\n")
  }

  def getRandomAliveSquad() = {
    val random = new Random()
    val aliveSquads = squadsInArmy.filter(_.isAlive)
    aliveSquads(random.nextInt(aliveSquads.size))
  }

  def getFullInfo: String = {
    val squadBuilder = StringBuilder.newBuilder
    squadBuilder.append(heroInArmy.getFullInfo).append("\n")
    squads.filter(_.isAlive).map(_.getFullInfo).foreach(squadBuilder.append(_).append("\n"))
    squadBuilder.toString
  }

  def updateArmy = {
    hero.updateSpellUsage
    squads.foreach(squad => squad.updateSquad)
  }
}
