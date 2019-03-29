import hero._
import main.{Squad, SquadInArmy}
import org.scalatest.{FlatSpec, PrivateMethodTester}

class TestCountAttackAndDefense extends FlatSpec with PrivateMethodTester {

  private val hero = new Hero("Test", 0, 0, 0, 0)

  class TestAttackBuff(private val goal : Squad, private val bonusAttack : Int) extends ContinuousSpell("Test attack buff", 3, 3) with AttackSpell {
    override def changeAttackValue: Int = bonusAttack
    override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy) = true
  }

  class TestAttackDebuff(private val goal : Squad, private val minusAttack : Int) extends ContinuousSpell("Test attack debuff", 3, 3) with AttackSpell {
    override def changeAttackValue: Int = -minusAttack
    override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy) = true
  }

  class TestDefenseBuff(private val goal : Squad, private val bonusDefense : Int) extends ContinuousSpell("Test defense buff", 3, 3) with DefenseSpell {
    override def changeDefenseValue: Int = bonusDefense
    override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy) = true
  }

  class TestDefenseDebuff(private val goal : Squad, private val minusDefense : Int) extends ContinuousSpell("Test defense debuff", 3, 3) with DefenseSpell {
    override def changeDefenseValue: Int = -minusDefense
    override def canBeActedOnSquad(goal: SquadInArmy, hero: HeroInArmy) = true
  }

  private def createSquad(attack: Int, defense: Int) =
    new Squad("test", 0, 0, 0, 0, attack, defense, 0)

  private def applySpells(squad: Squad, spells : List[Spell]) = spells.foreach(squad.receiveSpell(_, hero))

  "no spells" should " be " in {
    val attack = 10
    val defense = 0
    val squad = createSquad(attack, defense)
    assert(attack == squad.getAttack)
    assert(defense == squad.getDefense)
  }

  "buffs spells" should " be " in {
    val attack = 10
    val defense = 0
    val squad = createSquad(attack, defense)
    val spells = List(
      new TestAttackBuff(squad,3),
      new TestAttackBuff(squad,5),
      new TestDefenseBuff(squad, 3),
      new TestDefenseBuff(squad, 1),
    )
    applySpells(squad, spells)
    assert(attack + 8== squad.getAttack)
    assert(defense + 4== squad.getDefense)
  }

  "debuffs spells" should " be " in {
    val attack = 10
    val defense = 7
    val squad = createSquad(attack, defense)
    val spells = List(
      new TestAttackDebuff(squad,2),
      new TestAttackDebuff(squad,4),
      new TestDefenseDebuff(squad, 6),
      new TestDefenseDebuff(squad, 1),
    )
    applySpells(squad, spells)
    assert(attack - 6 == squad.getAttack)
    assert(defense - 7 == squad.getDefense)
  }

  "all spells" should " be " in {
    val attack = 10
    val defense = 10
    val squad = createSquad(attack, defense)
    val spells = List(
      new TestAttackBuff(squad,6),
      new TestAttackBuff(squad,3),
      new TestAttackDebuff(squad,7),
      new TestAttackDebuff(squad,1), // 6 + 3 - 7 - 1 = +1
      new TestDefenseBuff(squad, 3),
      new TestDefenseBuff(squad, 1),
      new TestDefenseDebuff(squad, 4),
      new TestDefenseDebuff(squad, 2), // 3 + 1 - 4 - 2 = -2
    )
    applySpells(squad, spells)
    assert(attack + 1 == squad.getAttack)
    assert(defense - 2 == squad.getDefense )
  }

}
