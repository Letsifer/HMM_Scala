class Army(val name: String, val hero: Hero) {

  var squads: List[Squad] = null

  override def toString: String = name

  def isAlive() = squads.exists(_.isAlive())
  def isNotAlive() = !isAlive()
}
