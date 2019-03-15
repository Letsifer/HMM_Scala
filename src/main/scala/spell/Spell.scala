package spell

trait Spell {
  def action(goal: Squad)
  def canBeActedOnSquad(goal : Squad)
}

abstract class ContinuusSpell(private val roundsToBe : Int) extends Spell {
  private var remainRounds = roundsToBe

  override def action(goal: Any): Unit = {
    //здесь будет указано наложение бафа\дебафа на отряд, т.е. добавление бафа\дебафа в список таких заклинаний
  }

  private def decreaseSteps() = {
    remainRounds = remainRounds - 1
    if (remainRounds == 0) {
      endSpell()
    }
  }

  private def endSpell() = {

  }
}

abstract  class Buff(private val roundsToBe : Int) extends ContinuusSpell {

}

abstract  class Debuff(private val roundsToBe : Int) extends ContinuusSpell {

}

//Заклинания на изменение защиты отряда
trait DefenseSpell {
  def changeDefenseValue : Int
}

object StoneSpellSkill extends Buff(3) with DefenseSpell {
  override def changeDefenseValue: Int = 3
}

object DestructionSpell extends Debuff(3) with DefenseSpell {
  override def changeDefenseValue: Int = -3
}