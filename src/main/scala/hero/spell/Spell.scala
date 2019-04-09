package hero.spell

/**
  * Заклинание, действующее на отряд.
  */
trait Spell {
  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  def getTitle: String
  def fullSpell = getTitle

  override def toString: String = getTitle
}

trait StraightDamageSpell extends Spell {
  def damage : Int
}

/**
  * Заклинание, которое действует несколько ходов.
  */
abstract class ContinuousSpell(private val rounds : Int) extends Spell {
  private var remainRounds = rounds
  override def fullSpell: String = super.toString + " (" + remainRounds + ")"

  /**
    * Уменьшение оставшегося количества раундов, на которое будет действовать это заклинание.
    * @return Закончилось ли действий заклинания.
    */
  def decreaseRounds : Boolean = {
    remainRounds -= 1
    remainRounds == 0
  }
}

/**
  * Заклинание на изменение защиты отряда.
  */
trait DefenseSpell extends Spell {
  /**
    * Изменение защиты отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  def changeDefenseValue: Int
}

/**
  * Заклинания на изменение атаки отряда.
  */
trait AttackSpell extends Spell {
  /**
    * Изменение атаки отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  def changeAttackValue: Int
}