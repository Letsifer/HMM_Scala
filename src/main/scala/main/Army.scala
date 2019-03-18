package main

import hero.{Hero, HeroBean}

import scala.util.Random

class Army(val name: String, private val heroBean: HeroBean, private val beans : List[SquadBean]) {

  val hero = new Hero(heroBean.name, heroBean.attack, heroBean.defense, this)
  val squads: List[Squad] = beans.map(b => new Squad(b.name, b.creaturesInSquadAtStart, b.maxHealth, b.minAttack, b.maxAttack, b.attack, b.defence, b.speed, this))


  override def toString: String = name

  def isAlive = squads.exists(_.isAlive)
  def isNotAlive = !isAlive

  def getBattleResult = {
    squads.map(squad => s"$squad: Было ${squad.creaturesInSquadAtStart}, потеряно ${squad.getLostCreaturesNumberWhileBattle}").mkString("\n")
  }


  def getRandomAliveSquad() : Squad = {
    val random = new Random()
    val aliveSquads = squads.filter(_.isAlive)
    aliveSquads(random.nextInt(aliveSquads.size))
  }
}
