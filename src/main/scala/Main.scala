object Main extends App {

  override def main(args: Array[String]) = {
    val firstArmy = new Army("Замок",
      new Hero("Сорша", 2, 2), List(
        new Squad("Копейщики", 20, 10, 1, 3, 4, 5),
        new Squad("Грифоны", 8, 25, 3, 6, 8, 8),
        new Squad("Рыцари", 7, 35, 6, 9, 10, 12)
      ))
    val secondArmy = new Army("Твердыня",
      new Hero("Гретчин", 4, 0), List(
        new Squad("Гоблины", 40, 5, 1, 2, 4, 2),
        new Squad("Наездники на волках", 10, 10, 2, 4, 7, 5),
        new Squad("Огры", 6, 40, 6, 12, 13, 7)
      ))

    while (firstArmy.isAlive() && secondArmy.isAlive()) {
      action(firstArmy, secondArmy)
      action(secondArmy, firstArmy)
    }
  }

  def action(attacker: Army, defender: Army) = {
    if (attacker.isAlive() && defender.isAlive()) {
      val attackResult = attacker.action(defender)
      if (attackResult.getKilledCreatures() > 0) {
        println(s"${attackResult.attacker.name} нанесли ${attackResult.getDamage()} урона, убито ${attackResult.getKilledCreatures()} ${attackResult.defender.name}")
      } else {
        println(s"${attackResult.attacker.name} нанесли ${attackResult.getDamage()} урона")
      }
      if (!defender.isAlive()) {
        println(s"${attacker.name} победил")
      }
    }
  }
}
