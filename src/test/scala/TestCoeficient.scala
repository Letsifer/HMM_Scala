import hero.Hero
import main.{Army, Process, SquadBean}
import org.scalatest.{FlatSpec, PrivateMethodTester}

class TestCoeficient extends FlatSpec with PrivateMethodTester {

  private def routineTesting(attackerHeroAttack: Int, attackerSquadAttack: Int, defenderHeroDefense: Int, defenderSquadDefense: Int, expectedResult: Double) = {
    val attackerHero = new Hero("attacker_hero", attackerHeroAttack, 0)
    val attacker = new SquadBean("attacker", 0, 0, 0, 0, attackerSquadAttack, 0, 0)
    val attackerArmy = new Army("attacker_army", attackerHero, List(attacker))

    val defenderHero = new Hero("defender_hero", 0, defenderHeroDefense)
    val defender = new SquadBean("defender", 0, 0, 0, 0, 0, defenderSquadDefense, 0)
    val defenderArmy = new Army("defender_army", defenderHero, List(defender))

    val process = new Process(attackerArmy, defenderArmy)
    val method = PrivateMethod[Double]('calculateAttackModifier)
    val result = process.invokePrivate(method(attacker, attackerHero, defender, defenderHero))
    assert(result === expectedResult)
  }

  "Attack modifier" should "be 1" in {
    routineTesting(0, 10, 0, 10, 1.0)
  }

  "Attack modifier" should "be 1.05" in {
    routineTesting(1, 10, 0, 10, 1.05)
  }

  "Attack modifier" should "be 1.5" in {
    routineTesting(10, 10, 0, 10, 1.5)
  }

  "Attack modifier" should "be 0.95" in {
    routineTesting(0, 10, 1, 10, 0.95)
  }

  "Attack modifier" should "be 0.91" in {
    routineTesting(0, 10, 2, 10, 0.91)
  }
}
