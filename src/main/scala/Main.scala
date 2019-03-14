object Main extends App {

  override def main(args: Array[String]) = {
    val castle = new Army("Замок", new Hero("Сорша", 2, 2))
    val castleSquads = List(
      new Squad("Алебардщики", 20, 10, 2, 3, 6, 5, 5, castle),
      new Squad("Грифоны", 8, 25, 3, 6, 8, 8, 6, castle),
      new Squad("Рыцари", 9, 35, 6, 9, 10, 12, 5, castle)
    )
    castle.squads = castleSquads
    val stronghold = new Army("Твердыня",      new Hero("Гретчин", 4, 0))
    val strongholdArmy = List(
      new Squad("Хогбгоблины", 80, 5, 1, 2, 5, 3, 7, stronghold),
      new Squad("Наездники на волках", 10, 10, 2, 4, 7, 5, 6, stronghold),
      new Squad("Огры", 6, 40, 6, 12, 13, 7, 4, stronghold)
    )
    stronghold.squads = strongholdArmy
    val result = new Process(castle, stronghold).battle()
    result match {
      case BattleResult.FIRST_WIN => println(s"$castle win")
      case BattleResult.SECOND_WIN => println(s"$stronghold win")
    }

  }


}
