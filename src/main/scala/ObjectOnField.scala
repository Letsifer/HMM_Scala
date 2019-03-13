trait ObjectOnField {
  def nameOnField(): String
}

class SquadOnField(val squad: Squad) extends ObjectOnField {
  override def nameOnField() = squad.toString.take(1)

  def isThisSquadOnField(squad: Squad) = squad == this.squad

  def areSquadsFromTheSameArmy(other: Squad) = squad.areSquadsFromTheSameArmy(other)
}
