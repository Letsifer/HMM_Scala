package hero.newspell

class StoneSkinSpell(private val rounds : Int) extends ContinuousSpell(rounds) with DefenseSpell {
  /**
    * Изменение защиты отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  override def changeDefenseValue: Int = 3

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Каменная кожа"
}

class DestructionSpell(private val rounds : Int) extends ContinuousSpell(rounds) with DefenseSpell {
  /**
    * Изменение защиты отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  override def changeDefenseValue: Int = -3

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Разрушение"
}

class BloodLustSpell (private val rounds : Int) extends ContinuousSpell(rounds) with AttackSpell {
  /**
    * Изменение атаки отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  override def changeAttackValue: Int = 3

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Жажда крови"
}

class WeaknessSpell (private val rounds : Int) extends ContinuousSpell(rounds) with AttackSpell {
  /**
    * Изменение атаки отряда - может быть как на увеличение (положительное число), так и на уменьшение (отрицательное число).
    *
    * @return
    */
  override def changeAttackValue: Int = -3

  /**
    * Наименование заклинания.
    *
    * TODO потом заменить на название параметра, по которому будет лежать ниаименование.
    *
    * @return
    */
  override def getTitle: String = "Слабость"
}