import main.{Squad, SquadAttackResult}
import org.scalatest.{FlatSpec, PrivateMethodTester}

class TestReceiveDamage extends FlatSpec with PrivateMethodTester {

  private def createSquad(creatures : Int, maxHealth : Int) =
    new Squad("test", creatures, maxHealth, 0, 0, 0, 0, 0)

  private def routingTosting(squad : Squad, damage : Int) :SquadAttackResult = {
    val method = PrivateMethod[SquadAttackResult]('receiveDamage)
    squad.invokePrivate(method(damage))
  }

  "Squad " should " be more than defeated" in {
    val creatures = 10; val health = 10
    val damage = 101
    val result = routingTosting(createSquad(creatures, health), damage)
    assert(result.wereAllCreaturesInDefenderSquadKilled == true)
    assert(result.killedCreatures == creatures)
    assert(result.resultDamage == damage)
  }

  "Squad " should " be more equal defeated" in {
    val creatures = 100; val health = 10
    val damage = 1000
    val result = routingTosting(createSquad(creatures, health), damage)
    assert(result.wereAllCreaturesInDefenderSquadKilled == true)
    assert(result.killedCreatures == creatures)
    assert(result.resultDamage == damage)
  }

  "Squad " should " remain with 1 hp" in {
    val creatures = 100; val health = 10
    val damage = 999
    val result = routingTosting(createSquad(creatures, health), damage)
    assert(result.wereAllCreaturesInDefenderSquadKilled == false)
    assert(result.killedCreatures == creatures - 1)
    assert(result.resultDamage == damage)
    assert(result.areCreaturesKilled() == true)
  }

  "Squad " should "receive 2 attacks" in {
    val creatures = 100; val health = 10
    val squad = createSquad(creatures, health)
    val firstDamage = 7
    val resultOfFirstAttack = routingTosting(squad, firstDamage)
    assert(resultOfFirstAttack.wereAllCreaturesInDefenderSquadKilled == false)
    assert(resultOfFirstAttack.areCreaturesKilled() == false)
    assert(squad.getCurrentHealth == 3)
    val secondDamage = 14
    val resultOfSecondAttack = routingTosting(squad, secondDamage)
    assert(resultOfSecondAttack.wereAllCreaturesInDefenderSquadKilled == false)
    assert(resultOfSecondAttack.areCreaturesKilled() == true)
    assert(resultOfSecondAttack.killedCreatures == 2)
    assert(squad.getCurrentHealth == 9)
  }
}
