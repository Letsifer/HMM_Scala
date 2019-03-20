package field

import main.SquadInArmy

trait ObjectOnField {
  def nameOnField: String
}

object EmptyField extends ObjectOnField {
  override def nameOnField: String = " "
}

object MountainOnField extends ObjectOnField {
  override def nameOnField: String = "*"
}

class SquadOnField(val squadInArmy: SquadInArmy) extends ObjectOnField {
  override def nameOnField = squadInArmy.getShortTitle

  def isNotAlive = !squadInArmy.isAlive

  def isThisSquadOnField(other: SquadInArmy) = squadInArmy == other

  def isOtherSquadOnField(other: SquadInArmy) = !isThisSquadOnField(other)

  def areSquadsFromTheSameArmy(other: SquadInArmy) = squadInArmy.areSquadsFromTheSameArmy(other)
}
