trait ObjectOnField {
  def nameOnField(): String
}

object EmptyField extends ObjectOnField { //TODO change nulls to empty field
  override def nameOnField(): String = " "
}

class SquadOnField(val squad: Squad) extends ObjectOnField {
  override def nameOnField() = if (squad.isAlive()) squad.toString.take(1) else " "

  def isNotAlive() = !squad.isAlive()

  def isThisSquadOnField(squad: Squad) = squad == this.squad

  def areSquadsFromTheSameArmy(other: Squad) = squad.areSquadsFromTheSameArmy(other)
}
