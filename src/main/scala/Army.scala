class Army(val name: String, val hero: Hero, private val beans : List[SquadBean]) {

  val squads: List[Squad] = beans.map(b => new Squad(b.name, b.creaturesInSquadAtStart, b.maxHealth, b.minAttack, b.maxAttack, b.attack, b.defence, b.speed, this))


  override def toString: String = name

  def isAlive = squads.exists(_.isAlive)
  def isNotAlive = !isAlive

  def getBattleResult = {
    squads.map(squad => s"$squad: Было ${squad.creaturesInSquadAtStart}, потеряно ${squad.getLostCreaturesNumberWhileBattle}").mkString("\n")
  }
}
